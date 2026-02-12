package br.leg.camara.lexmljsonixspringbootstarter.service;

import java.io.StringReader;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


import br.gov.lexml.parser.pl.ws.data.jaxb.ParserResultado;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Classe que representa metadados simplificados de uma proposição.
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Proposicao {
	
	private Integer idIdentificacao;
	private String descricaoIdentificacao;
	private String descricaoIdentificacaoExtensa;
	
	private String sigla;
	private String numero;
	private Integer ano;
	
	private Integer idProcesso;
	private Integer idDocumentoManifestacao;
	private Integer codMateriaMigradaMATE;
	
	private Boolean emElaboracao;
	
	private String estadoProposicao;
	
	private String ementa;
	
	private String dataLeitura;
	
	private Long idDocumentoItemDigital;
	
	private String idSdlegDocumentoItemDigital;
	private String urlDownloadItemDigitalZip;
	
	private LocalDate dataPublicacao;
	private LocalDate dataLimiteRecebimentoEmendas;
	private String labelPrazoRecebimentoEmendas;
	private String labelTramitacao;
	private String tipoMateriaOrcamentaria;
	private Boolean indTextoAprovadoTurnoOuSegundoTurno;
	
	
	private Set<String> caracteristicasDetectadas;
	private static final Pattern XML_STYLESHEET_PI =
	        Pattern.compile("<\\?xml-stylesheet[^>]*\\?>");
	
	
	
	@JsonCreator
    public Proposicao(
            @JsonProperty("idIdentificacao") Integer idIdentificacao,
            @JsonProperty("descricaoIdentificacao") String descricaoIdentificacao,
            @JsonProperty("descricaoIdentificacaoExtensa") String descricaoIdentificacaoExtensa,
            @JsonProperty("sigla") String sigla,
            @JsonProperty("numero") String numero,
            @JsonProperty("ano") Integer ano,
            @JsonProperty("idProcesso") Integer idProcesso,
            @JsonProperty("idDocumentoManifestacao") Integer idDocumentoManifestacao,
            @JsonProperty("codMateriaMigradaMATE") Integer codMateriaMigradaMATE,
            @JsonProperty("emElaboracao") Boolean emElaboracao,
            @JsonProperty("estadoProposicao") String estadoProposicao,
            @JsonProperty("ementa") String ementa,
            @JsonProperty("dataLeitura") String dataLeitura,
            @JsonProperty("idDocumentoItemDigital") Long idDocumentoItemDigital,
            @JsonProperty("idSdlegDocumentoItemDigital") String idSdlegDocumentoItemDigital,
            @JsonProperty("urlDownloadItemDigitalZip") String urlDownloadItemDigitalZip,
            @JsonProperty("dataPublicacao") LocalDate dataPublicacao,
            @JsonProperty("dataLimiteRecebimentoEmendas") LocalDate dataLimiteRecebimentoEmendas,
            @JsonProperty("labelPrazoRecebimentoEmendas") String labelPrazoRecebimentoEmendas,
            @JsonProperty("labelTramitacao") String labelTramitacao,
            @JsonProperty("tipoMateriaOrcamentaria") String tipoMateriaOrcamentaria,
            @JsonProperty("indTextoAprovadoTurnoOuSegundoTurno") Boolean indTextoAprovadoTurnoOuSegundoTurno,
            @JsonProperty("metadadosParser") Map<String, Object> metadadosParser) {
	    
	    this.idIdentificacao = idIdentificacao;
        this.descricaoIdentificacao = descricaoIdentificacao;
        this.descricaoIdentificacaoExtensa = descricaoIdentificacaoExtensa;
        this.sigla = sigla;
        this.numero = numero;
        this.ano = ano;
        this.idProcesso = idProcesso;
        this.idDocumentoManifestacao = idDocumentoManifestacao;
        this.codMateriaMigradaMATE = codMateriaMigradaMATE;
        this.emElaboracao = emElaboracao;
        this.estadoProposicao = estadoProposicao;
        this.ementa = ementa;
        this.dataLeitura = dataLeitura;
        this.idDocumentoItemDigital = idDocumentoItemDigital;
        this.idSdlegDocumentoItemDigital = idSdlegDocumentoItemDigital;
        this.urlDownloadItemDigitalZip = urlDownloadItemDigitalZip;
        this.dataPublicacao = dataPublicacao;
        this.dataLimiteRecebimentoEmendas = dataLimiteRecebimentoEmendas;
        this.labelPrazoRecebimentoEmendas = labelPrazoRecebimentoEmendas;
        this.labelTramitacao = labelTramitacao;
        this.tipoMateriaOrcamentaria = tipoMateriaOrcamentaria;
        this.indTextoAprovadoTurnoOuSegundoTurno = indTextoAprovadoTurnoOuSegundoTurno;
	    
	    
	    if (metadadosParser != null) {
            unpackMetadados(metadadosParser);
        }
        
	    
	}	
	
    private void unpackMetadados(Map<String, Object> metadadosParser) {
        if (!metadadosParser.containsKey("saidaParser")) {
            return;
        }
        
       
        

        String xmlContent = (String) metadadosParser.get("saidaParser");
        String cleanXml = XML_STYLESHEET_PI
		        .matcher(xmlContent)
		        .replaceFirst("");
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ParserResultado.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            
            try (StringReader reader = new StringReader(cleanXml)) {
                ParserResultado resultado = (ParserResultado) unmarshaller.unmarshal(reader);
                
                if (resultado != null && resultado.getResultado() != null) {
                	Boolean temFalhasNoParser = resultado.getResultado().getFalhas().getFalha().size() > 0;
                	
                	if (Boolean.TRUE.equals(temFalhasNoParser)) {
                        this.idDocumentoItemDigital = null;
                        this.idSdlegDocumentoItemDigital = null;
                        this.urlDownloadItemDigitalZip = null;
                    }
                	
                    this.caracteristicasDetectadas = resultado.getResultado().getCaracteristicas()
                            .getCaracteristica().stream()
                            .filter(c -> Boolean.TRUE.equals(c.isPresente()))
                            .map(c -> c.getDescricao())
                            .collect(Collectors.toSet());
                    
                    if (this.caracteristicasDetectadas != null && !this.caracteristicasDetectadas.isEmpty()) {
                        
                        Set<String> caracteristicasImpeditivas = new HashSet<>(Arrays.asList("possui imagem","possui tabela na articulacao","possui pena"));
                        
                        boolean hasCaracteristicasImpeditivas = !Collections.disjoint(caracteristicasImpeditivas,this.caracteristicasDetectadas);
                        
                        if (hasCaracteristicasImpeditivas) {
                            this.idDocumentoItemDigital = null;
                            this.idSdlegDocumentoItemDigital = null;
                            this.urlDownloadItemDigitalZip = null;                           
                        }
                    }
                }
            }
        }catch (JAXBException e) {
			throw new RuntimeException(e);
		}
    }
	

}
