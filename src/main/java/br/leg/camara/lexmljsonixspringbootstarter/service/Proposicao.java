package br.leg.camara.lexmljsonixspringbootstarter.service;

import java.time.LocalDate;

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

}
