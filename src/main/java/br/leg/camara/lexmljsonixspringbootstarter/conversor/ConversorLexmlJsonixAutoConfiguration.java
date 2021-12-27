package br.leg.camara.lexmljsonixspringbootstarter.conversor;

import java.io.FileNotFoundException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(ConversorLexmlJsonix.class)
@EnableConfigurationProperties(LexmlJsonixProperties.class)
public class ConversorLexmlJsonixAutoConfiguration {
	
	@Value("${spring.config.activate.on-profile:unknown}") 
	private String activeProfile;
	
	@Bean
	@ConditionalOnMissingBean
	public ConversorLexmlJsonix jsonixService(LexmlJsonixProperties lexmlJsonixProperties) throws FileNotFoundException {
		return new ConversorLexmlJsonixImpl(lexmlJsonixProperties, activeProfile);
	}

}
