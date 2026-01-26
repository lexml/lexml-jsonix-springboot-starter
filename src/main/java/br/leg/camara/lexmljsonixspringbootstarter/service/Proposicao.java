package br.leg.camara.lexmljsonixspringbootstarter.service;

import java.io.StringReader;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

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
	
	
	private List<String> caracteristicasDetectadas;
	
	private static final Pattern XML_STYLESHEET_PI =
	        Pattern.compile("<\\?xml-stylesheet[^>]*\\?>");
	
	@JsonProperty("metadadosParser")
    private void unpackMetadados(Map<String, Object> metadadosParser) {
        if (metadadosParser == null || !metadadosParser.containsKey("saidaParser")) {
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
                            .collect(Collectors.toList());
                }
            }
        }catch (JAXBException e) {
			throw new RuntimeException(e);
		}
    }
	

}
