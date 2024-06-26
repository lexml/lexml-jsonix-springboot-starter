package br.leg.camara.lexmljsonixspringbootstarter.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.NotBlank;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import br.leg.camara.lexmljsonixspringbootstarter.conversor.ConversorLexmlJsonix;
import br.leg.camara.lexmljsonixspringbootstarter.conversor.LexmlJsonixProperties;
import br.leg.camara.lexmljsonixspringbootstarter.utils.ZipUtils;

public class LexmlJsonixServiceImpl implements LexmlJsonixService {

	private ConversorLexmlJsonix conversorLexmlJsonix;
	private LexmlJsonixProperties jsonixProperties;
	private RestTemplate restTemplate;
	
	private final String SIGLA_MPV = "MPV";
	
	private final int ANO_LIMITE = 2022; // Até esse ano não existem MPs em tramitação

	public LexmlJsonixServiceImpl(ConversorLexmlJsonix conversorLexmlJsonix, LexmlJsonixProperties jsonixProperties, RestTemplate restTemplate) {
		this.conversorLexmlJsonix = conversorLexmlJsonix;
		this.jsonixProperties = jsonixProperties;
		this.restTemplate = restTemplate;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Proposicao> getProposicoes(@NotBlank String sigla, @NotBlank Integer ano, String numero, Boolean carregarDatasDeMPs, Boolean preferirSubstitutivo) {
		List<Proposicao> proposicoes = this.getProposicoesExchange(sigla, ano, numero, preferirSubstitutivo);
		Set<Integer> idsDoma = new HashSet<>();
    	
    	// Retira duplicações
		proposicoes = proposicoes.stream().filter(p -> idsDoma.add(p.getIdDocumentoManifestacao()))
    			.collect(Collectors.toList());
    	
		if(Boolean.TRUE.equals(carregarDatasDeMPs) && SIGLA_MPV.equalsIgnoreCase(sigla)) {
			return this.carregarDatasMPVs(proposicoes);
		}
		
		return proposicoes;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Proposicao> getProposicoesEmTramitacao(@NotBlank String sigla, Boolean carregarDatasDeMPs,
													   Boolean preferirSubstitutivo) {
		List<Proposicao> proposicoes = this.getProposicoesEmTramitacaoExchange(sigla, preferirSubstitutivo);
		Set<Integer> idsDoma = new HashSet<>();
		
		// Retira duplicações e MPs anteriores a 2023
		proposicoes = proposicoes.stream().filter(p -> idsDoma.add(p.getIdDocumentoManifestacao()) && p.getAno() > ANO_LIMITE)
    			.collect(Collectors.toList());
    	
		if(Boolean.TRUE.equals(carregarDatasDeMPs) && SIGLA_MPV.equalsIgnoreCase(sigla)) {
			return this.carregarDatasMPVs(proposicoes);
		}
		return proposicoes;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Proposicao getProposicao(@NotBlank String sigla, @NotBlank Integer ano, @NotBlank String numero, @NotBlank Boolean preferirSubstitutivo) {
		List<Proposicao> proposicoes = getProposicoesExchange(sigla, ano, numero, preferirSubstitutivo);
		return ObjectUtils.isEmpty(proposicoes) ? null : proposicoes.get(0);
	}

	@Override
	public String getTextoProposicaoAsXml(String sigla, Integer ano, String numero, Boolean preferirSubstitutivo) {
		Proposicao proposicao = getProposicao(sigla, ano, numero, preferirSubstitutivo);
		if (ObjectUtils.isEmpty(proposicao.getIdSdlegDocumentoItemDigital()))
			return null;
		return getTextoProposicaoAsXml(proposicao.getIdSdlegDocumentoItemDigital());
	}

	@Override
	public String getTextoProposicaoAsXml(String idSdlegDocumentoItemDigital) {
		try {
			byte[] zip = getLexmlZip(idSdlegDocumentoItemDigital);
	        byte[] xml = ZipUtils.readEntry(zip, "texto.xml");
	        return xml==null ? null : new String(xml);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * Este método utiliza um bean que implementa a interface ConversorLexmlJsonix para realizar a conversão
	 * de xml para json do texto Lexml da proposição.
	 *
	 */
	@Override
	public String getTextoProposicaoAsJson(String sigla, Integer ano, String numero, Boolean preferirSubstitutivo) {
		Proposicao proposicao = getProposicao(sigla, ano, numero, preferirSubstitutivo);
		if (ObjectUtils.isEmpty(proposicao.getIdSdlegDocumentoItemDigital()))
			return null;
		return getTextoProposicaoAsJson(proposicao.getIdSdlegDocumentoItemDigital());
	}

	/**
	 * {@inheritDoc}
	 *
	 * Este método utiliza um bean que implementa a interface ConversorLexmlJsonix para realizar a conversão
	 * de xml para json do texto Lexml da proposição.
	 *
	 */
	@Override
	public String getTextoProposicaoAsJson(String idSdlegDocumentoItemDigital) {
		String xml = getTextoProposicaoAsXml(idSdlegDocumentoItemDigital);
		return conversorLexmlJsonix.xmlToJson(xml);
	}

	private byte[] getLexmlZip(String idSdlegDocumentoItemDigital) {
		String url = jsonixProperties.getUrlSdleg() + "/" + idSdlegDocumentoItemDigital;
		return restTemplate.getForObject(url, byte[].class);
	}
	
	private List<Proposicao> getProposicoesExchange(@NotBlank String sigla, @NotBlank Integer ano, String numero, Boolean preferirSubstitutivo) {
		String complemento = (Boolean.FALSE.equals(preferirSubstitutivo) ? "t=i" : "") + (ObjectUtils.isEmpty(numero) ? "" : "&numero=" + numero);
		String urlTemplate = jsonixProperties.getUrlProposicoes() + "/%s/%d?%s";
		String url = String.format(urlTemplate, sigla, ano, complemento);
		ResponseEntity<List<Proposicao>> responseEntity = restTemplate
				.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Proposicao>>(){});
		return responseEntity.getBody();
	}
	
	private List<Proposicao> getProposicoesEmTramitacaoExchange(@NotBlank String sigla, Boolean preferirSubstitutivo) {
		String urlTemplate = jsonixProperties.getUrlProposicoes() + "/%s?e=A" + (Boolean.FALSE.equals(preferirSubstitutivo) ? "?t=i" : "");
		String url = String.format(urlTemplate, sigla);
		ResponseEntity<List<Proposicao>> responseEntity = restTemplate
				.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Proposicao>>(){});
		return responseEntity.getBody();
	}
	
	private List<Proposicao> carregarDatasMPVs(List<Proposicao> proposicoes) {
		List<Integer> idsProcessos = proposicoes
			.stream()
			.parallel()
			.filter(p -> p.getAno() > ANO_LIMITE)
			.map(proposicao -> proposicao.getIdProcesso())
			.collect(Collectors.toList());
		
		List<DatasMP> datasMPs = this.getDatasMPsExchange(idsProcessos);		
		proposicoes.forEach(proposicao -> {
			Optional<DatasMP> dataMPVOptional = datasMPs
				.stream()
				.filter(dataMPV -> dataMPV.getIdProcesso().equals(proposicao.getIdProcesso()))
				.findFirst();
			
			if(dataMPVOptional.isPresent()) {
				DatasMP dataMPV = dataMPVOptional.get();
				
				proposicao.setDataPublicacao(dataMPV.getDataPublicacao());
				proposicao.setDataLimiteRecebimentoEmendas(dataMPV.getDataLimiteRecebimentoEmendas());
				proposicao.setLabelPrazoRecebimentoEmendas(this.formartarLabel(dataMPV));
				proposicao.setLabelTramitacao(this.formatarLabelTramitacao(proposicao, dataMPV));
			}
			else {
				proposicao.setLabelPrazoRecebimentoEmendas("encerrado");
				proposicao.setLabelTramitacao("");
			}
			
		});
		
		return proposicoes;
	}
	
	private String formatarLabelTramitacao(Proposicao proposicao, DatasMP dataMPV) {
		if (proposicao.getEstadoProposicao().equalsIgnoreCase("Encerrado")) {
			return "";
		}
		long daysBetween = ChronoUnit.DAYS.between(proposicao.getDataPublicacao(), LocalDate.now()) + 1;
		return daysBetween + "º dia";
	}

	private List<DatasMP> getDatasMPsExchange(List<Integer> idsProcessos) {
		if(idsProcessos.isEmpty()) {
			return Arrays.asList();
		}
		String url = jsonixProperties.getUrlDatasMPVs() + idsProcessos.stream()
			.map(idProcesso -> idProcesso.toString())
			.reduce("?", (queryParam, idProcesso) ->  queryParam + "idsProcessos=" + idProcesso + "&");
		System.out.println("URL: " + url);
		ResponseEntity<List<DatasMP>> responseEntity = restTemplate
				.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<DatasMP>>(){});
		return responseEntity.getBody();
	}
	
	private String formartarLabel(DatasMP datasMP) {
		return this.formartarLabel(datasMP.getDataLimiteRecebimentoEmendas());
	}	
	
	private String formartarLabel(LocalDate dataLimite) {
		
		if(dataLimite == null) {
			return "";
		}
		
		LocalDate now = LocalDate.now();
		String dataLimiteFormatada = dataLimite.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		
		if(dataLimite.isBefore(now)) {
			return "encerrado";
		} else if(dataLimite.isEqual(now)) {			
			return dataLimiteFormatada.concat(" (hoje)");
		} else if(!now.isAfter(dataLimite)) {
			long daysBetween = ChronoUnit.DAYS.between(now, dataLimite) + 1;
			return dataLimiteFormatada.concat(" (").concat(String.valueOf(daysBetween)).concat(daysBetween > 1L ? " dias" : " dia").concat(")");
		} else {
			return "";
		}
	}

}
