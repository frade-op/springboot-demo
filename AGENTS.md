# AGENTS.md

Contexto para assistentes de codificacao que auxiliarem neste repositorio.

## Sobre este projeto

Projeto pessoal de estudo de Spring Boot. O objetivo principal e aprendizado: a maior parte do codigo, das decisoes de arquitetura e da implementacao e escrita e pensada pelo proprio autor, estudando os padroes corretos antes de aplica-los.

**Os assistentes devem atuar como apoio, nao como autores principais do codigo.** Use-os para:

- Validar ou revisar codigo ja escrito pelo autor.
- Explicar erros e apontar causas, sem necessariamente reescrever tudo.
- Sugerir boas praticas e padroes do ecossistema Spring Boot.
- Tirar duvidas pontuais sobre configuracao, dependencias e conceitos.

Evite reescrever grandes trechos de codigo ou gerar funcionalidades completas sem que o autor peca explicitamente. Prefira apontar o problema e a diretriz de correcao, permitindo que o autor implemente quando possivel.

## Stack

- Java 21
- Maven, via Maven Wrapper (`mvnw.cmd` no Windows)
- Spring Boot 4.1.1
- Spring Web MVC, para endpoints HTTP
- Spring Data JPA (Hibernate), para persistencia
- Spring Security, para autenticacao e autorizacao
- Spring Boot Actuator, para monitoramento
- Spring Boot Validation, para validacao de dados de entrada
- MySQL Connector/J (`com.mysql:mysql-connector-j`), driver do banco
- `spring-dotenv`, para carregar variaveis do arquivo `.env`

## Estrutura de pacotes

```text
src/main/java/com/example/demo/
├── DemoApplication.java
├── config/         configuracoes Spring
├── controller/     endpoints REST (@RestController)
├── model/          entidades JPA (@Entity)
├── repository/     interfaces JpaRepository
└── service/        regras de negocio e integracoes com Spring
```

Novas classes devem seguir essa separacao por camada.

## Banco de dados

- MySQL local, schema `springboot-demo`.
- Conexao configurada em `src/main/resources/application.properties`, usando variaveis `DB_PORT`, `DB_USER`, `DB_PASSWORD` carregadas do `.env` (arquivo nao versionado, listado no `.gitignore`).
- `spring.jpa.hibernate.ddl-auto=none`: o Hibernate nao cria nem altera tabelas automaticamente. As tabelas ja existem no schema e sao geridas manualmente.
- Colunas no banco usam `snake_case` (ex.: `id_user`). Campos das entidades JPA devem usar `camelCase` (ex.: `idUser`); a estrategia padrao do Spring Boot converte entre os formatos sem precisar de `@Column` quando seguem essa convencao.
- A entidade `User` possui `passwordHash`, mapeado para `password_hash`; essa coluna deve existir no banco antes de executar consultas que a utilizem.

## Seguranca

- Nunca commitar credenciais. Usuarios e senhas de conexao ficam apenas no `.env` local.
- Nunca armazenar ou comparar senhas em texto puro. Usar o `PasswordEncoder` BCrypt para codificar e verificar senhas.
- Nunca incluir o hash de senha em respostas da API; preferir DTOs para entrada e saida.
- O `DatabaseUserDetailsService` carrega o usuario pelo e-mail e transforma papeis do banco em authorities com prefixo `ROLE_`.
- A configuracao `SecurityFilterChain` e as regras de acesso por endpoint ainda sao o proximo passo de implementacao; nao presumir que as rotas ja tenham autorizacao por papel.
- Ao configurar CSRF, considerar como os clientes da API enviarao requisicoes e nao desativar a protecao sem uma decisao consciente.

## Executar e testar

```powershell
.\mvnw.cmd spring-boot:run
.\mvnw.cmd test
.\mvnw.cmd compile
```