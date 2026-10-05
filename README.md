# Spring Boot Demo

Projeto base para experimentos e aprendizado com Spring Boot. Use este repositório para criar endpoints, explorar configuracoes, escrever testes e entender o ciclo de desenvolvimento de uma aplicacao web Java.

> **Sobre o uso de assistentes de codificação:** este é um projeto de estudo. A maior parte do código é escrita e pensada pelo autor, estudando os padrões corretos antes de aplicá-los. Os assistentes são usados como apoio pontual — para validar decisões, explicar erros e sugerir boas práticas — não para gerar funcionalidades do zero. Veja [AGENTS.md](AGENTS.md) para as diretrizes de colaboração.

## Tecnologias

- Java 21
- Maven, pelo Maven Wrapper incluido no projeto
- Spring Boot 4.1.1
- Spring Web MVC, para criar endpoints HTTP
- Spring Boot Actuator, para recursos de monitoramento
- Spring Web MVC Test, para testes da camada web
- Spring Data JPA, para persistencia com Hibernate
- Spring Security, para autenticacao e autorizacao (em implementacao)
- Spring Boot Validation, para validar dados de entrada
- MySQL Connector/J (`com.mysql:mysql-connector-j`), driver de conexao com MySQL

## Estrutura

```text
src/
|- main/
|  |- java/com/example/demo/
|  |  |- DemoApplication.java
|  |  |- config/
|  |  |  |- PasswordEncoderConfig.java
|  |  |  `- SecurityConfig.java
|  |  |- controller/
|  |  |  |- CsrfController.java
|  |  |  |- HelloController.java
|  |  |  |- ProductController.java
|  |  |  `- UserController.java
|  |  |- dto/
|  |  |  `- NewUser.java
|  |  |- model/
|  |  |  |- Product.java
|  |  |  |- User.java
|  |  |  |- Role.java
|  |  |  `- UserRoles.java
|  |  |- repository/
|  |     |- ProductRepository.java
|  |     |- UserRepository.java
|  |     |- RoleRepository.java
|  |     `- UserRolesRepository.java
|  |  `- service/
|  |     `- DatabaseUserDetailsService.java
|  `- resources/
|     `- application.properties
`- test/
   `- java/com/example/demo/
      `- DemoApplicationTests.java
```

Na raiz tambem ficam as regras de estilo para assistentes de codificacao (`AGENTS.md`, `.github/`, `.cursor/`, `.windsurf/`, `.clinerules/` e `.opencode/`), descritas na secao [Regras para assistentes](#regras-para-assistentes-caveman).

A classe `DemoApplication` e o ponto de entrada da aplicacao. Controllers, models e repositories ficam separados por camada dentro de `com.example.demo`, para serem encontrados automaticamente pelo Spring Boot.

## Modelo de dados atual

- `Product`: produtos, vinculados a uma categoria por `idCategory` (sem relacionamento JPA ainda, apenas o id bruto).
- `User`: usuarios da aplicacao (`name`, `email`, `passwordHash`). O hash da senha e omitido da serializacao JSON.
- `Role`: papeis/perfis (`name`), usados para controle de acesso.
- `UserRoles`: tabela de associacao entre `User` e `Role` (`idUser`, `idRole`), tambem sem relacionamento JPA ainda.

Os campos das entidades seguem `camelCase` (ex.: `idUser`, `idCategory`), e o Hibernate converte automaticamente para `snake_case` nas colunas do banco (`id_user`, `id_category`) via `PhysicalNamingStrategy` padrao do Spring Boot.

## Configuracao atual

O arquivo `src/main/resources/application.properties` contem:

```properties
spring.application.name=demo
spring.datasource.url=jdbc:mysql://localhost:${DB_PORT:}/springboot-demo
spring.datasource.username=${DB_USER:}
spring.datasource.password=${DB_PASSWORD:}
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
```

As credenciais sao lidas de variaveis locais e nao devem ser adicionadas ao repositorio. Como `ddl-auto=none`, alteracoes no schema MySQL precisam ser feitas manualmente.

Sem configuracao diferente, a aplicacao inicia na porta `8080` por padrao.

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

## Atualizar dependencias Maven

Sempre que uma dependencia for adicionada ou alterada no `pom.xml`, siga estes passos:

1. Edite o `pom.xml` e adicione o bloco `<dependency>` com `groupId` e `artifactId`. Como o projeto usa o `spring-boot-starter-parent` como parent, a `<version>` normalmente nao precisa ser informada, pois ja e gerenciada automaticamente.
2. Baixe as novas dependencias executando, na raiz do projeto:

```powershell
.\mvnw.cmd package
```

Esse comando baixa as dependencias do repositorio Maven, compila o projeto e roda os testes. Use `BUILD SUCCESS` no final do log como confirmacao de que tudo foi resolvido corretamente.

Se quiser apenas baixar as dependencias, sem compilar nem testar:

```powershell
.\mvnw.cmd dependency:resolve
```

Em caso de erro `BUILD FAILURE`, verifique a mensagem: geralmente indica `groupId`/`artifactId` incorretos ou versao ausente para dependencias que nao sao gerenciadas pelo parent do Spring Boot.

## Regras para assistentes (caveman)

O repositorio versiona a regra "caveman", que pede respostas curtas e diretas dos assistentes de IA, sem perder o conteudo tecnico. Como o projeto e de estudo e nao uma aplicacao real, os arquivos ficam commitados:

| Ferramenta | Arquivo |
|---|---|
| Geral / varias ferramentas | `AGENTS.md` (bloco `caveman-begin`/`caveman-end` no fim) |
| GitHub Copilot | `.github/copilot-instructions.md` |
| Cursor | `.cursor/rules/caveman.mdc` |
| Windsurf | `.windsurf/rules/caveman.md` |
| Cline | `.clinerules/caveman.md` |
| OpenCode | `.opencode/AGENTS.md` |

A regra afeta apenas o estilo das respostas; codigo, comentarios, commits, PRs e documentacao continuam em modo normal. Para desativar numa conversa, use "stop caveman" ou "normal mode". Os blocos entre os marcadores sao gerenciados pela instalacao: nao edite manualmente.

## Proximos experimentos

- Criar controllers para `Role` e `UserRoles` (ainda so existem os repositories).
- Adicionar propriedades ao `application.properties`, como `server.port`.
- Criar testes para endpoints HTTP.
- Consultar endpoints do Actuator, como `http://localhost:8080/actuator/health`.

## Autenticacao e autorizacao

Status: **autenticacao basica concluida e verificada de ponta a ponta** (cadastro, login com HTTP Basic, papeis do banco e `/users` restrito a `ADMIN`).

### O que esta implementado

- `spring-boot-starter-security` como dependencia.
- Bean `PasswordEncoder` baseado em BCrypt (`PasswordEncoderConfig`).
- `User.passwordHash`, com o getter ignorado pelo Jackson (o hash nunca sai nas respostas).
- `DatabaseUserDetailsService`: busca o usuario por e-mail e le os papeis em `user_roles`/`roles`. O nome do papel no banco vira authority com prefixo `ROLE_`.
- `SecurityConfig` com HTTP Basic e as regras:

| Rota | Acesso |
|---|---|
| `/hello`, `/csrf`, `/signup`, `/error` | publico |
| `/users`, `/users/**` | papel `ADMIN` |
| rotas de produtos e demais | autenticado |

- Protecao CSRF mantida, pois a API tambem sera consumida por navegador.
- `POST /signup` (publico): recebe o DTO `NewUser` (`name`, `email`, `password`) validado com `@Valid` (`@NotBlank`, `@Email`), grava o hash BCrypt e retorna `409` se o e-mail ja existir. A coluna `users.email` tem constraint `UNIQUE` (verificado).
- O cadastro **nao** atribui papeis e nunca deve permitir que o cliente escolha um. O papel `ADMIN` e inserido manualmente em `user_roles`.

### Como usar (Postman ou outro cliente)

1. `GET /csrf` sem autenticacao. A resposta traz `headerName` (`X-CSRF-TOKEN`) e `token`, e o cookie `JSESSIONID`.
2. `POST /signup` com **Auth type: No Auth**, o header `X-CSRF-TOKEN: <token>`, o cookie da etapa anterior e o corpo JSON `{"name": "...", "email": "...", "password": "..."}`.
3. No banco, associe o usuario ao papel `ADMIN`: `roles.name` deve ser exatamente `ADMIN` (nao `ROLE_ADMIN`), e deve existir a linha correspondente em `user_roles`.
4. `GET /users` com **Basic Auth** (e-mail e senha). O CSRF so e exigido em metodos que alteram dados, entao o header e desnecessario no `GET`.

### Respostas e diagnostico

- `401`: nao autenticado ou credenciais invalidas. Rotas publicas tambem retornam `401` se o cliente enviar um header `Authorization` Basic invalido (desativar a autenticacao herdada nessas rotas).
- `403`: autenticado sem o papel exigido, ou requisicao que altera dados sem token CSRF valido. Se `/users` retornar `403` com login correto, verificar os dados de papel com:

```sql
SELECT u.id_user, u.email, ur.id_role, CONCAT('[', r.name, ']') AS role_name
FROM users u
LEFT JOIN user_roles ur ON ur.id_user = u.id_user
LEFT JOIN roles r ON r.id_role = ur.id_role;
```

  Causas comuns: nome gravado como `ROLE_ADMIN` (vira `ROLE_ROLE_ADMIN`), grafia/caixa/espaco diferente, ou `user_roles` ligado a outro `id_user`. As authorities sao lidas a cada requisicao, sem reiniciar a aplicacao.
- O token CSRF vai em `X-CSRF-TOKEN` (com o cookie de sessao), **nunca** em `Authorization`/Bearer.
- `/error` precisa estar liberado em `SecurityConfig`; sem isso, erros reais (`400`, `403`, `409`) apareciam como `401`.

## Pendencias e proximos passos

### Variaveis do `.env`

Em um teste com `.\mvnw.cmd test`, `DB_PORT`, `DB_USER` e `DB_PASSWORD` nao chegaram ao Spring (erro `Access denied for user '<usuario do SO>'@'localhost' (using password: NO)`); o teste passou ao exportar as variaveis no ambiente manualmente. Confirmar se `spring-boot:run` e os testes carregam o `.env`. Se nao, testar uma versao mais recente do `spring-dotenv` ou usar `spring.config.import=optional:file:.env[.properties]`.

### Testes automatizados de seguranca

Hoje so existe `contextLoads`, e o fluxo foi validado manualmente. Cobrir com MockMvc e `spring-security-test` (nova dependencia de teste):

- `401` sem login em rota protegida.
- `403` com usuario autenticado sem o papel `ADMIN` em `/users`.
- `200` com papel `ADMIN` em `/users`.
- `/signup` sem token CSRF retorna `403`; com token valido e corpo valido retorna `200`; e-mail duplicado retorna `409`; corpo invalido retorna `400`.
- Rotas publicas (`/hello`, `/csrf`) acessiveis sem login.

### Melhorias de seguranca

- **Papel padrao no cadastro:** decidir se todo novo usuario recebe um papel basico (ex.: `USER`), sempre atribuido pelo servidor.
- **Politica de senha:** adicionar tamanho minimo (`@Size`) e, se desejado, outras regras ao `NewUser`.
- **DTO de saida no `/signup`:** hoje retorna a entidade `User` (seguro por causa do `@JsonIgnore`); preferir um DTO de resposta.
- **Demais endpoints:** `ProductController` ainda recebe e retorna entidades JPA e nao valida entrada; migrar para DTOs com `@Valid`. Seus `POST` tambem exigem o token CSRF.
- **Respostas de erro:** padronizar o formato dos erros de validacao.
- **Producao:** HTTP Basic envia as credenciais a cada requisicao; usar somente com HTTPS. Avaliar limitar tentativas de login e, mais adiante, migrar para sessao ou JWT.

### Front-end simples (futuro)

Criar uma aplicacao web simples para exercitar o fluxo pelo navegador:

- Telas de cadastro e login, e uma tela restrita que consome `/users` (visivel apenas para `ADMIN`).
- Obter o token em `/csrf` e envia-lo no header `X-CSRF-TOKEN` nas chamadas que alteram dados, enviando os cookies (`credentials: 'include'` em `fetch`).
- Definir a estrategia de autenticacao no navegador: HTTP Basic reenvia as credenciais a cada chamada e nao tem logout real; considerar migrar para login com sessao (`formLogin`/endpoint de login) ao implementar o front.
- Se o front rodar em outra origem (ex.: servidor de desenvolvimento em outra porta), configurar CORS de forma restrita, permitindo credenciais apenas para a origem conhecida, sem desativar o CSRF.