package br.leg.camara.lexmljsonixspringbootstarter.conversor;

/**
 * Define métodos para conversão de xml para json e vice-versa.
 * @author Robson Barros
 *
 */
public interface ConversorLexmlJsonix {
	
	/**
	 * Converte uma string xml, em formato Lexml, para json.
	 * @param xml string xml a ser convertida para json.
	 * @return string json.
	 */
	public String xmlToJson(String xml);
		
	/**
	 * Converte uma string xml, em formato Lexml, para json.
	 * @param xml string xml a ser convertida para json.
	 * @return string json.
	 */
	public String jsonToXml(String json);
	
}
