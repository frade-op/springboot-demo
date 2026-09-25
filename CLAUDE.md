# CLAUDE.md

Contexto para assistentes de IA (Claude, Copilot, etc.) que auxiliarem neste repositório.

## Sobre este projeto

Projeto pessoal de estudo de Spring Boot. O objetivo principal é aprendizado: a maior parte do código, das decisões de arquitetura e da implementação é escrita e pensada pelo próprio autor, estudando os padrões corretos antes de aplicá-los.

**A IA deve atuar como apoio, não como autora principal do código.** Use a IA para:

- Validar ou revisar codigo ja escrito pelo autor.
- Explicar erros e apontar causas, sem necessariamente reescrever tudo.
- Sugerir boas praticas e padroes do ecossistema Spring Boot.
- Tirar duvidas pontuais sobre configuracao, dependencias e conceitos.

Evite reescrever grandes trechos de codigo ou gerar features completas sem que o autor peça explicitamente. Prefira apontar o problema e a diretriz de correção, permitindo que o autor implemente quando possível.

## Stack

- Java 21
- Maven, via Maven Wrapper (`mvnw.cmd` no Windows)
- Spring Boot 4.1.1
- Spring Web MVC, para endpoints HTTP
- Spring Data JPA (Hibernate), para persistencia
- Spring Boot Actuator, para monitoramento
- Spring Boot Validation, para validacao de dados de entrada
- MySQL Connector/J (`com.mysql:mysql-connector-j`), driver do banco
- `spring-dotenv`, para carregar variaveis do arquivo `.env`

## Estrutura de pacotes

```text
src/main/java/com/example/demo/
├── DemoApplication.java
├── controller/     endpoints REST (@RestController)
├── model/          entidades JPA (@Entity)
└── repository/     interfaces JpaRepository
```

Novas classes devem seguir essa mesma separação por camada.

## Banco de dados

- MySQL local, schema `springboot-demo`.
- Conexao configurada em `src/main/resources/application.properties`, usando variaveis `DB_PORT`, `DB_USER`, `DB_PASSWORD` carregadas do `.env` (arquivo nao versionado, listado no `.gitignore`).
- `spring.jpa.hibernate.ddl-auto=none`: o Hibernate nao cria nem altera tabelas automaticamente. As tabelas ja existem no schema e sao geridas manualmente.

## Regras de seguranca

- Nunca commitar credenciais. Senhas e usuarios ficam apenas no `.env` local.
- Nao sugerir hardcode de senha em `application.properties` ou em codigo Java.

## Executar e testar

```powershell
.\mvnw.cmd spring-boot:run
.\mvnw.cmd test
.\mvnw.cmd compile
```
