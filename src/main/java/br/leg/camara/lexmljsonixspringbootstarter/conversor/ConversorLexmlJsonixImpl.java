package br.leg.camara.lexmljsonixspringbootstarter.conversor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang3.SystemUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;

import br.leg.camara.lexmljsonixspringbootstarter.utils.CliUtils;
import br.leg.camara.lexmljsonixspringbootstarter.utils.DadosInOut;

/**
 * Classe usada para converter, de xml para json e vice-versa, o texto em formato Lexml de uma proposição.
 * Esta classe utiliza o executável gerado a partir do código do projeto 
 * <a href="https://github.com/lexml/jsonix-lexml">https://github.com/lexml/jsonix-lexml</a> para realizar a conversão.
 * 
 * @author Robson Barros
 * @see <a href="https://github.com/lexml/jsonix-lexml">https://github.com/lexml/jsonix-lexml</a>
 */
public class ConversorLexmlJsonixImpl implements ConversorLexmlJsonix {
	
	private String activeProfile;
	
	private LexmlJsonixProperties lexmlJsonixProperties;

	public ConversorLexmlJsonixImpl(LexmlJsonixProperties lexmlJsonixProperties, String activeProfile) throws FileNotFoundException {
		this.lexmlJsonixProperties = lexmlJsonixProperties;
		this.activeProfile = activeProfile;
		checarExistenciaArquivo(lexmlJsonixProperties.getCli());
	}

	private void checarExistenciaArquivo(String filename) throws FileNotFoundException {
		if ("test".equals(activeProfile))
			return;
		
		if (!(new File(filename).isFile()))
			throw new FileNotFoundException(filename);		
	}

	@Override
	public String xmlToJson(String xml) {
		try {
			if (lexmlJsonixProperties.isUsarEntradaSaidaPadraoDuranteConversao())
				return converterXmlToJsonixUsandoEntradaSaidaPadrao(xml);
			else
				return converterXmlToJsonixUsandoArquivo(xml);			
		} catch (Exception e) {
			throw new RuntimeException("Erro ao converter de xml para jsonix: " + e.getMessage(), e);
		}
	}
	
	@Override
	public String jsonToXml(String json) {
		try {
			if (lexmlJsonixProperties.isUsarEntradaSaidaPadraoDuranteConversao())
				return converterJsonixToXmlUsandoEntradaSaidaPadrao(json);
			else
				return converterJsonixToXmlUsandoArquivo(json);			
		} catch (Exception e) {
			throw new RuntimeException("Erro ao converter de json para xml: " + e.getMessage(), e);
		}
	}
	
	private String converterJsonixToXmlUsandoEntradaSaidaPadrao(String json) {
		return converterUsandoEntradaSaidaPadrao(json, "toxml");
	}

	private String converterXmlToJsonixUsandoEntradaSaidaPadrao(String xml) {
		return converterUsandoEntradaSaidaPadrao(xml, "tojson");
	}

	private String converterJsonixToXmlUsandoArquivo(String json) throws IOException {
		return converterUsandoArquivo(json, "toxml");
	}

	private String converterXmlToJsonixUsandoArquivo(String xml) throws IOException {
		return converterUsandoArquivo(xml, "tojson");
	}
	
	private String converterUsandoArquivo(String source, String formatoSaida) throws IOException {
		Path xmlPath = writeStringToDisk(source);
		
		String argumentoIn = xmlPath.toString();
		String argumentoOut = xmlPath.getParent().toString() + File.separator + "jsonix.json";
		String cli = String.format("%s %s %s -o %s", lexmlJsonixProperties.getCli(), formatoSaida, argumentoIn, argumentoOut);

		executarComando(cli, null);
		
		String result = new String(Files.readAllBytes(Paths.get(argumentoOut)));
		
		if (lexmlJsonixProperties.isRemoverDiretorioAposConversao())
			deleteFolder(xmlPath.getParent());
		
		return result;
	}

	private String converterUsandoEntradaSaidaPadrao(String source, String formatoSaida) {
		String argumentoIn = "-"; // lê da entrada padrão
		String argumentoOut = "-"; // escreve na entrada padrão
		String cli = String.format("%s %s %s -o %s", lexmlJsonixProperties.getCli(), formatoSaida, argumentoIn, argumentoOut);
		
		DadosInOut dadosInOut = new DadosInOut();
		dadosInOut.setIn(source);
		
		executarComando(cli, dadosInOut);
		
		return dadosInOut.getOut();				
	}
	
	private void executarComando(String comando, DadosInOut dadosInOut) {
		if (SystemUtils.IS_OS_WINDOWS)
			CliUtils.execWindowsCommand(comando, dadosInOut);
		else
			CliUtils.execLinuxCommand(comando, dadosInOut);		
	}

	private void deleteFolder(Path folder) throws IOException {
		FileUtils.deleteDirectory(folder.toFile());
	}

	private Path writeStringToDisk(String value) throws IOException {
		Path tempDirWithPrefix = Files.createTempDirectory("lexml");
		String filename = tempDirWithPrefix.toString() + File.separator + "texto.xml";
		return Files.write(Paths.get(filename), value.getBytes());
	}

}
