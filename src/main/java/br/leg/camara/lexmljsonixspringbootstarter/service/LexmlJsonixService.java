package br.leg.camara.lexmljsonixspringbootstarter.service;

import java.util.List;

import javax.validation.constraints.NotBlank;

/**
 * Especificação dos métodos para retornar dados de proposições e texto em formato Lexml.
 * 
 * @author Robson Barros
 */
public interface LexmlJsonixService {
	
	/**
	 * Retorna uma lista de proposições com base nos parâmetros de pesquisa.
	 * 
	 * @param sigla  Sigla da proposição. Parâmetro obrigatório.
	 * @param ano    Ano da proposição. Parâmetro obrigatório.
	 * @param numero Número da proposição. Parâmetro opcional.
	 * @return Retorna uma lista de proposições
	 */
	public List<Proposicao> getProposicoes(@NotBlank String sigla, @NotBlank Integer ano, String numero);
	
	/**
	 * Retorna uma lista de proposições em tramitação dada a sigla do tipo da proposição.
	 * 
	 * @param sigla  Sigla da proposição. Parâmetro obrigatório.
	 * @return Retorna uma lista de proposições
	 */
	public List<Proposicao> getProposicoesEmTramitacao(@NotBlank String sigla);
	
	/**
	 * Retorna uma proposição.
	 * 
	 * @param sigla  Sigla da proposição. Parâmetro obrigatório.
	 * @param ano    Ano da proposição. Parâmetro obrigatório.
	 * @param numero Número da proposição. Parâmetro obrigatório.
	 * @return um objeto {@code Proposicao}
	 */
	public Proposicao getProposicao(@NotBlank String sigla, @NotBlank Integer ano, @NotBlank String numero);
	
	/**
	 * Retorna o texto Lexml de uma proposição em formato xml.
	 * 
	 * @param sigla  Sigla da proposição. Parâmetro obrigatório.
	 * @param ano    Ano da proposição. Parâmetro obrigatório.
	 * @param numero Número da proposição. Parâmetro obrigatório.
	 * @return string xml do texto de uma proposição.
	 */
	public String getTextoProposicaoAsXml(String sigla, Integer ano, String numero);
	
	/**
	 * Retorna o texto Lexml de uma proposição em formato xml.
	 * 
	 * @param idSdlegDocumentoItemDigital id do documento da proposição.
	 * @return string xml do texto de uma proposição.
	 */
	public String getTextoProposicaoAsXml(String idSdlegDocumentoItemDigital);
	
	/**
	 * Retorna o texto Lexml de uma proposição em formato json.
	 * 
	 * @param sigla  Sigla da proposição. Parâmetro obrigatório.
	 * @param ano    Ano da proposição. Parâmetro obrigatório.
	 * @param numero Número da proposição. Parâmetro obrigatório.
	 * @return string json do texto de uma proposição.
	 */
	public String getTextoProposicaoAsJson(String sigla, Integer ano, String numero);
	
	/**
	 * Retorna o texto Lexml de uma proposição em formato json.
	 * 
	 * @param idSdlegDocumentoItemDigital id do documento da proposição.
	 * @return string json do texto de uma proposição.
	 */	
	public String getTextoProposicaoAsJson(String idSdlegDocumentoItemDigital);

}
