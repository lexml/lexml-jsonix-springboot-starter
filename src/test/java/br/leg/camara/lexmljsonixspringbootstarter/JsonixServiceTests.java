package br.leg.camara.lexmljsonixspringbootstarter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.web.client.RestTemplate;

import br.leg.camara.lexmljsonixspringbootstarter.conversor.ConversorLexmlJsonix;
import br.leg.camara.lexmljsonixspringbootstarter.conversor.ConversorLexmlJsonixImpl;
import br.leg.camara.lexmljsonixspringbootstarter.conversor.LexmlJsonixProperties;
import br.leg.camara.lexmljsonixspringbootstarter.service.LexmlJsonixService;
import br.leg.camara.lexmljsonixspringbootstarter.service.LexmlJsonixServiceImpl;

public class JsonixServiceTests {
	
	private static LexmlJsonixService lexmlJsonixService;
	private static ConversorLexmlJsonix conversorLexmlJsonix;
	
	static {
		try {
			lexmlJsonixService = getLexmlJsonixService();
			conversorLexmlJsonix = getConversorLexmlJsonix();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	public static void main(String[] args) throws IOException {
		teste1();
		teste2();
		teste3();
		teste4();
	}

	private static void teste1() throws FileNotFoundException {
		Object object = lexmlJsonixService.getTextoProposicaoAsJson("MPV", 2021, "1061");
		printObject(object);
	}
	
	private static void teste2() throws FileNotFoundException {
		Object object = lexmlJsonixService.getTextoProposicaoAsJson("156849ea-879a-4f7e-b8ac-6d7921961fc6");
		printObject(object);
	}

	private static void teste3() throws IOException {
		String xml = new String(Files.readAllBytes(Paths.get("c:\\Desenv\\jsonix\\bin\\texto.xml")));
		Object object = conversorLexmlJsonix.xmlToJson(xml);
		printObject(object);
	}
	
	private static void teste4() throws IOException {
		String xml = new String(Files.readAllBytes(Paths.get("c:\\Desenv\\jsonix\\bin\\texto.xml")));
		Object object = conversorLexmlJsonix.xmlToJson(xml);
		printObject(conversorLexmlJsonix.jsonToXml(object.toString()));
	}

	private static LexmlJsonixService getLexmlJsonixService() throws FileNotFoundException {
		return new LexmlJsonixServiceImpl(getConversorLexmlJsonix(), getLexmlJsonixProperties(), new RestTemplate());
	}
	
	private static void printObject(Object object) {
		System.out.println();
		System.out.println(object.toString());
		System.out.println();						
	}

	private static ConversorLexmlJsonix getConversorLexmlJsonix() throws FileNotFoundException {
		return new ConversorLexmlJsonixImpl(getLexmlJsonixProperties(), "");
	}

	private static LexmlJsonixProperties getLexmlJsonixProperties() {
		LexmlJsonixProperties jsonixProperties = new LexmlJsonixProperties();
		jsonixProperties.setCli("c:\\Desenv\\jsonix\\bin\\jsonix-lexml-win.exe");
		return jsonixProperties;
	}

}
