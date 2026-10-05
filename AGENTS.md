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
├── dto/            objetos de entrada e saida da API
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
- A `SecurityFilterChain` (`SecurityConfig`) usa HTTP Basic. `/hello`, `/csrf`, `/signup` e `/error` sao publicos, `/users` exige `ADMIN` e as demais rotas exigem autenticacao. A autenticacao basica esta concluida e foi verificada de ponta a ponta.
- `/error` deve permanecer liberado; sem isso, erros reais (`400`, `403`, `409`) aparecem como `401`.
- CSRF permanece ativo porque a API tambem sera consumida por navegador; nao desativar sem uma decisao consciente. Clientes obtem o token em `/csrf` e o enviam no header `X-CSRF-TOKEN` (com o cookie de sessao), nunca em `Authorization`/Bearer. O CSRF so e exigido em metodos que alteram dados.
- O cadastro (`POST /signup`, DTO `NewUser`) grava o hash BCrypt e nao atribui papeis. O papel `ADMIN` e inserido manualmente em `user_roles`; o cadastro nunca deve permitir que o usuario escolha papeis. `roles.name` deve ser `ADMIN`, sem o prefixo `ROLE_` (o servico adiciona).
- Diagnostico de `401`/`403`: `401` tambem ocorre em rotas publicas se o cliente enviar um `Authorization` Basic invalido; `403` em `/users` com login correto indica problema nos dados de `roles`/`user_roles` ou, em metodos que alteram dados, ausencia de token CSRF. Detalhes e SQL de verificacao no README.

## Pendencias e proximos passos

Detalhes completos na secao "Pendencias e proximos passos" do README. Ao ajudar, tratar como apoio: apontar problemas e diretrizes, deixando a implementacao com o autor, salvo pedido explicito.

- `.env`: `.\mvnw.cmd test` nao carregou `DB_PORT`, `DB_USER` e `DB_PASSWORD` (erro `Access denied ... (using password: NO)`); funcionou com as variaveis exportadas no ambiente. Verificar `spring-boot:run` e, se necessario, usar versao mais recente do `spring-dotenv` ou `spring.config.import=optional:file:.env[.properties]`.
- Testes de seguranca com MockMvc e `spring-security-test`: `401` sem login, `403` sem `ADMIN`, `200` com `ADMIN`, e `/signup` sem/com token CSRF, e-mail duplicado (`409`) e corpo invalido (`400`).
- Melhorias: papel padrao no cadastro atribuido pelo servidor, politica de senha (`@Size`), DTO de saida no `/signup`, DTOs e `@Valid` em `ProductController`, padronizar erros de validacao, HTTPS em producao, limitar tentativas de login.
- Front-end simples (futuro) para cadastro, login e tela restrita a `ADMIN`: obter token em `/csrf`, enviar cookies (`credentials: 'include'`), avaliar login com sessao em vez de HTTP Basic e, se houver outra origem, CORS restrito sem desativar o CSRF.
## Regras de estilo para assistentes (caveman)

O projeto versiona a regra "caveman", que pede respostas curtas e diretas dos assistentes (sem cerimonia, mantendo o conteudo tecnico). Por ser um projeto de estudo, e nao uma aplicacao real, os arquivos ficam no repositorio, um por ferramenta:

- `AGENTS.md` (bloco entre `<!-- caveman-begin -->` e `<!-- caveman-end -->`, no fim deste arquivo)
- `.github/copilot-instructions.md` (GitHub Copilot)
- `.cursor/rules/caveman.mdc` (Cursor)
- `.windsurf/rules/caveman.md` (Windsurf)
- `.clinerules/caveman.md` (Cline)
- `.opencode/AGENTS.md` (OpenCode)

A propria regra define que codigo, comentarios, commits, PRs e documentacao sao escritos em modo normal; ela vale so para o estilo das respostas. Nao editar manualmente os blocos entre os marcadores `caveman-begin`/`caveman-end`, pois sao gerenciados pela instalacao e podem ser sobrescritos. Para desativar, usar "stop caveman" ou "normal mode" na conversa.

## Executar e testar

```powershell
.\mvnw.cmd spring-boot:run
.\mvnw.cmd test
.\mvnw.cmd compile
```

<!-- caveman-begin -->
Respond terse like smart caveman. All technical substance stay. Only fluff die.

Rules:
- Answer first: Answer, then reason, then next step.
- Kill ceremony: No greeting, hedging, pleasantries, recap, or closer.
- Short word: "fix" not "implement a solution for".
- Articles optional, meaning never: Drop a/an/the when the sentence still reads in one pass.
- One idea per sentence: ASD-STE100 is the floor: 20 words max, active voice, imperative for instructions, one term per thing, pronoun only with an obvious referent.
- Payload verbatim: Code blocks unchanged.
- Tool runs: bounded status: No text between routine calls.
- User's language: Compress the style, not the language.
- Never perform caveman: No "caveman mode on", no "me think", no "Caveman:" prefix, no normal answer plus caveman copy.

Switch: /caveman (default), /ultracave (fragments, each fact once), /megacave (Classical Chinese 文言文)
Stop: "stop caveman" or "normal mode"

Auto-Clarity: plain prose for security warnings, irreversible actions, step order a fragment could scramble, user confused. Resume after.

Boundaries: code, comments, commits, PRs, docs written normal.
Floor: code, commands, paths, numbers and error strings verbatim; never drop not/never/no/only.
<!-- caveman-end -->
