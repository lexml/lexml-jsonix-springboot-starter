package br.leg.camara.lexmljsonixspringbootstarter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import br.leg.camara.lexmljsonixspringbootstarter.utils.CliUtils;
import br.leg.camara.lexmljsonixspringbootstarter.utils.DadosInOut;

public class CliUtilsTests {
	
	public static void main(String[] args) throws IOException {
		teste1();
		teste2();
	}
	
	private static void teste1() {
		String exe = "c:\\desenv\\jsonix\\bin\\jsonix-lexml-win.exe";
		String in = "c:\\desenv\\jsonix\\bin\\texto.xml";
		String out = "c:\\desenv\\jsonix\\bin\\texto.json";
		String cli = String.format("%s tojson %s -o %s", exe, in, out);
		CliUtils.execWindowsCommand(cli);		
	}
	
	private static void teste2() throws IOException {
		String filePath = "c:\\desenv\\jsonix\\bin\\texto2.xml";
		String xml = new String (Files.readAllBytes(Paths.get(filePath)));
		DadosInOut dados = new DadosInOut();
		dados.setIn(xml);

		String exe = "c:\\desenv\\jsonix\\bin\\jsonix-lexml-win.exe";
		String in = "-";
		String out = "-";
		String cli = String.format("%s tojson %s -o %s", exe, in, out);
		CliUtils.execWindowsCommand(cli, dados);
		System.out.println(dados.getOut());
	}

}
