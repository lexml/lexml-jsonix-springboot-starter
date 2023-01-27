package br.leg.camara.lexmljsonixspringbootstarter.service;

import java.io.IOException;
import java.util.List;

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

	public LexmlJsonixServiceImpl(ConversorLexmlJsonix conversorLexmlJsonix, LexmlJsonixProperties jsonixProperties, RestTemplate restTemplate) {
		this.conversorLexmlJsonix = conversorLexmlJsonix;
		this.jsonixProperties = jsonixProperties;
		this.restTemplate = restTemplate;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Proposicao> getProposicoes(@NotBlank String sigla, @NotBlank Integer ano, String numero) {
		String complemento = ObjectUtils.isEmpty(numero) ? "" : "?numero=" + numero;
		String urlTemplate = jsonixProperties.getUrlProposicoes() + "/%s/%d%s";
		String url = String.format(urlTemplate, sigla, ano, complemento);
		ResponseEntity<List<Proposicao>> responseEntity = restTemplate
				.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Proposicao>>(){});
		return responseEntity.getBody();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Proposicao> getProposicoesEmTramitacao(@NotBlank String sigla) {
		String urlTemplate = jsonixProperties.getUrlProposicoes() + "/%s?e=A";
		String url = String.format(urlTemplate, sigla);
		ResponseEntity<List<Proposicao>> responseEntity = restTemplate
				.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Proposicao>>(){});
		return responseEntity.getBody();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Proposicao getProposicao(@NotBlank String sigla, @NotBlank Integer ano, @NotBlank String numero) {
		List<Proposicao> proposicoes = getProposicoes(sigla, ano, numero);
		return ObjectUtils.isEmpty(proposicoes) ? null : proposicoes.get(0);
	}

	@Override
	public String getTextoProposicaoAsXml(String sigla, Integer ano, String numero) {
		Proposicao proposicao = getProposicao(sigla, ano, numero);
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
	public String getTextoProposicaoAsJson(String sigla, Integer ano, String numero) {
		Proposicao proposicao = getProposicao(sigla, ano, numero);
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

}
