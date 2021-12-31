# lexml-jsonix-spring-boot-starter

Biblioteca Java que retorna o texto de uma proposição em formato jsonix. 
Para realizar a conversão, esta biblioteca utiliza o conversor jsonix-lexml. Maiores informações sobre o conversor jsonix-lexml podem ser encontradas em [https://github.com/lexml/jsonix-lexml](https://github.com/lexml/jsonix-lexml#jsonix-lexml).

## Como usar
- Adicionar a dependência no pom.xml
```xml
    <dependency>
        <groupId>a-definir</groupId>
        <artifactId>lexml-jsonix-spring-boot-starter</artifactId>
        <version>0.0.1</version>
    </dependency>
```

- Configurar a propriedade `jsonix.cli` no arquivo `application.properties` da sua aplicação Java, informando a localização do executável do conversor lexml-jsonix. Exemplo:

```yaml
    lexml-jsonix.cli=c:\\temp\\jsonix-lexml-win.exe
```

  Releases dos executáveis para os três ambientes suportados (Linux, MacOS X e Windows) podem ser encontrados em https://github.com/lexml/jsonix-lexml/releases/.

### Para converter de xml para json e vice-versa

- Instanciar um bean que implementa a interface `ConversorLexmlJsonix`. Essa interface define os métodos para conversão de um texto em formato Lexml para json e vice-versa. A classe `ConversorLexmlJsonixImpl` implementa essa interface e utiliza o executável mencionado no item anterior para realizar a conversão. Exemplo de uso:

```java
    package com.lexmljsonixteste.jsonix;

    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.MediaType;
    import org.springframework.web.bind.annotation.RequestBody;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;

    import br.leg.camara.lexmljsonixspringbootstarter.conversor.ConversorLexmlJsonix;

    @RestController
    @RequestMapping("lexml-jsonix")
    public class ConversorController {
        
        @Autowired
        private ConversorLexmlJsonix conversorLexmlJsonix;
        
        @RequestMapping(value = "xml-to-json", produces = MediaType.APPLICATION_JSON_VALUE)
        public String xmlToJson(@RequestBody String xml) {
            return conversorLexmlJsonix.xmlToJson(xml);
        }	
        
        @RequestMapping(value = "json-to-xml", produces = MediaType.APPLICATION_XML_VALUE)
        public String jsonToXml(@RequestBody String json) {
            return conversorLexmlJsonix.jsonToXml(json);
        }	
        
    }
```

### Para pesquisar proposições e obter texto em formato Lexml

- A classe `LexmlJsonixServiceImpl` implementa a interface `LexmlJsonixService` e disponibiliza métodos para listar proposições, bem como método para obter o texto de uma proposição em formato Lexml. Exemplo de uso:

```java
    package com.lexmljsonixteste.lex;

    import java.util.List;

    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.MediaType;
    import org.springframework.web.bind.annotation.CrossOrigin;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RequestParam;
    import org.springframework.web.bind.annotation.RestController;

    import br.leg.camara.lexmljsonixspringbootstarter.service.LexmlJsonixService;
    import br.leg.camara.lexmljsonixspringbootstarter.service.Proposicao;

    @RestController
    @CrossOrigin(origins = "*")
    public class ProposicoesController {
        
        @Autowired
        private LexmlJsonixService lexmlJsonixService;
            
        @GetMapping("proposicoes")
        public List<Proposicao> getProposicoes(@RequestParam String sigla, @RequestParam Integer ano, String numero) {
            return lexmlJsonixService.getProposicoes(sigla, ano, numero);
        }

        @GetMapping("proposicao")
        public Proposicao getProposicao(@RequestParam String sigla, @RequestParam Integer ano, @RequestParam String numero) {
            return lexmlJsonixService.getProposicao(sigla, ano, numero);
        }
        
        @RequestMapping(value = "proposicao/texto-lexml/xml", produces = MediaType.APPLICATION_XML_VALUE)
        public String getTextoProposicaoAsXml(@RequestParam String sigla, @RequestParam Integer ano, @RequestParam String numero) {
            return lexmlJsonixService.getTextoProposicaoAsXml(sigla, ano, numero);
        }	

        @RequestMapping(value = "proposicao/texto-lexml/xml/sdleg", produces = MediaType.APPLICATION_XML_VALUE)
        public String getTextoProposicaoAsXml(@RequestParam String idSdlegDocumentoItemDigital) {
            return lexmlJsonixService.getTextoProposicaoAsXml(idSdlegDocumentoItemDigital);
        }	
                
        @RequestMapping(value = "proposicao/texto-lexml/json", produces = MediaType.APPLICATION_JSON_VALUE)
        public String getTextoProposicaoAsJson(@RequestParam String sigla, @RequestParam Integer ano, @RequestParam String numero) {
            return lexmlJsonixService.getTextoProposicaoAsJson(sigla, ano, numero);
        }	

        @RequestMapping(value = "proposicao/texto-lexml/json/sdleg", produces = MediaType.APPLICATION_JSON_VALUE)
        public String getTextoProposicaoAsJson(@RequestParam String idSdlegDocumentoItemDigital) {
            return lexmlJsonixService.getTextoProposicaoAsJson(idSdlegDocumentoItemDigital);
        }	
                
    }
```

- A classe `LexmlJsonixServiceImpl` utiliza um serviço disponibilizado pelo Senado Federal para realizar a pesquisa de proposições e para obter o texto em formato Lexml da proposição.
  - A url para pesquisar proposições é: https://legis.senado.gov.br/legis/resources/lex/proposicoes
  - A url para obter o arquivo zip que contém o texto em formato lexml da proposição é: https://legis.senado.gov.br/sdleg-getter/documento/download

- Essas urls podem ser alteradas a partir do arquivo `application.properties` da sua aplicação, conforme o exemplo abaixo:

```yaml
    lexml-jsonix.url-proposicoes=http://nova-url-do-servico-de-pesquisa-de-proposicoes
    lexml-jsonix.url-sdleg=http://nova-url-do-servico-que-retorna-texto-lexml
```

**Observação:** As urls acima podem ser alteradas, mas o formato do retorno deve permanecer o mesmo para que a classe `LexmlJsonixServiceImpl` funcione corretamente. Caso você possua outros serviços que retornem dados equivalentes, você poderá criar uma nova classe que implementa a interface `LexmlJsonixService` e construir o código para tratar os dados retornados nos novos serviços.


