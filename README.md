# Spring Boot Demo

Projeto base para experimentos e aprendizado com Spring Boot. Use este repositório para criar endpoints, explorar configuracoes, escrever testes e entender o ciclo de desenvolvimento de uma aplicacao web Java.

## Tecnologias

- Java 21
- Maven, pelo Maven Wrapper incluido no projeto
- Spring Boot 4.1.1
- Spring Web MVC, para criar endpoints HTTP
- Spring Boot Actuator, para recursos de monitoramento
- Spring Web MVC Test, para testes da camada web

## Estrutura

```text
src/
|- main/
|  |- java/com/example/demo/
|  |  `- DemoApplication.java
|  `- resources/
|     `- application.properties
`- test/
   `- java/com/example/demo/
      `- DemoApplicationTests.java
```

A classe `DemoApplication` e o ponto de entrada da aplicacao. Classes como controllers devem ficar em `com.example.demo` ou em seus subpacotes, para serem encontradas automaticamente pelo Spring Boot.

## Configuracao atual

O arquivo `src/main/resources/application.properties` contem:

```properties
spring.application.name=demo
```

Essa propriedade define o nome da aplicacao como `demo`.

As demais configuracoes, como a porta HTTP, usam os padroes do Spring Boot. Por isso, a aplicacao inicia na porta `8080` por padrao.

## Executar

No Windows, execute na raiz do projeto:

```powershell
.\mvnw.cmd spring-boot:run
```

A aplicacao ficara disponivel em `http://localhost:8080`.

## Testar

Para executar os testes:

```powershell
.\mvnw.cmd test
```

## Proximos experimentos

- Criar um controller com `@RestController` e `@GetMapping`.
- Adicionar propriedades ao `application.properties`, como `server.port`.
- Criar testes para endpoints HTTP.
- Consultar endpoints do Actuator, como `http://localhost:8080/actuator/health`.
