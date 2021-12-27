package br.leg.camara.lexmljsonixspringbootstarter.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CliUtils {
	
	private CliUtils() {} 
	
	public static void execWindowsCommand(String comando) {
		execWindowsCommand(comando, null);
	}
	
	public static void execLinuxCommand(String comando) {
		execLinuxCommand(comando, null);
	}

	public static void execWindowsCommand(String comando, DadosInOut dadosInOut) {
		try {
			
			ProcessBuilder pb = new ProcessBuilder();
			pb.command("cmd.exe", "/c", comando);
			pb.redirectError();
			Process process = pb.start();
			
			if (dadosInOut==null) {
				process.waitFor();
			} else {
				prepararEntradaSaidaPadrao(process, dadosInOut);
				process.waitFor();
			}

			log.info("Comando executado com sucesso: " + comando);
			
		} catch (Exception e) {
			log.error("Erro ao executar comando Windows: " + comando, e);
		}		
	}
	
	public static void execLinuxCommand(String comando, DadosInOut dadosInOut) {

	    try {
	        ProcessBuilder pb = new ProcessBuilder("bash", "-c", comando);
	        pb.inheritIO();
	        Process process = pb.start();
	        
	        if (dadosInOut==null) {
	        	process.waitFor();	        	
	        } else {
				prepararEntradaSaidaPadrao(process, dadosInOut);
				process.waitFor();	        	
	        }
	        
	        log.info("Comando executado com sucesso: " + comando);
	    } catch (Exception e) {
	        log.error("Erro ao executar comando Linux: " + comando, e);
	    }
		    
	}
		
	private static void prepararEntradaSaidaPadrao(Process process, DadosInOut dadosInOut) throws IOException, InterruptedException {
		// Escreve na "entrada padrão"
		OutputStream stdin = process.getOutputStream();
		stdin.write(dadosInOut.getIn().getBytes());
		stdin.flush();
		stdin.close();
		
		// Lê da saída padrão
		dadosInOut.setOut(IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8));		
	}
		
}
