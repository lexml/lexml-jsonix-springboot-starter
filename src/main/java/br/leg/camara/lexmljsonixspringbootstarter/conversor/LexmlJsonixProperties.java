package br.leg.camara.lexmljsonixspringbootstarter.conversor;

import javax.validation.constraints.NotBlank;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import lombok.Data;

@ConfigurationProperties(prefix = "lexml-jsonix")
@Data
@Validated
public class LexmlJsonixProperties {
	
	/**
	 * Caminho e nome do executável do conversor jsonix
	 * @see <a href="https://github.com/lexml/jsonix-lexml#jsonix-lexml">github.com/lexml/jsonix-lexml#jsonix-lexml</a>
	 */
	@NotBlank
	private String cli;
	
	/**
	 * Url do serviço que retorna os metadados da proposição
	 */
	private String urlProposicoes = "https://legis.senado.gov.br/legis/resources/lex/proposicoes";
	
	/**
	 * Url do serviço que retorna o arquivo zip contendo o arquivo "Texto.xml" (que traz o texto da norma em formato lexml)
	 */
	private String urlSdleg = "https://legis.senado.gov.br/sdleg-getter/documento/download";
	
	/**
	 * Indica se a entrada e saída padrão podem ser usadas durante o processo de conversão do executável lexml-jsonix.
	 * @see <a href="https://github.com/lexml/jsonix-lexml#jsonix-lexml">github.com/lexml/jsonix-lexml#jsonix-lexml</a>
	 */
	private boolean usarEntradaSaidaPadraoDuranteConversao = true;
	
	/**
	 * Indica se o diretório temporário criado durante a conversão do xml para jsonix deve ser removido após o uso.
	 * Esse parâmetro só é utilizando quando "usar-entrada-saida-padrao-durante-conversao" é igual a false.
	 */
	private boolean removerDiretorioAposConversao = true;
}
