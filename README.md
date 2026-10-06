# Spring Boot Demo

Projeto de estudo com Spring Boot cujo objetivo e construir a API de catalogo de produtos de uma pequena loja de materiais de construcao. O sistema deve aplicar papeis e um fluxo de aprovacao para alteracoes no catalogo.

> **Sobre o uso de assistentes de codificação:** este é um projeto de estudo. A maior parte do código é escrita e pensada pelo autor, estudando os padrões corretos antes de aplicá-los. Os assistentes são usados como apoio pontual — para validar decisões, explicar erros e sugerir boas práticas — não para gerar funcionalidades do zero. Veja [AGENTS.md](AGENTS.md) para as diretrizes de colaboração.

## Objetivo e regras de negocio

- Representar o catalogo de produtos de uma pequena loja de materiais de construcao, incluindo nome, descricao e valor.
- `USER` pode consultar o catalogo e ver os valores dos produtos. O cadastro publico cria somente usuarios `USER`.
- `STAFF` pode propor alteracoes de nome, descricao e valor. As propostas nao alteram o catalogo publicado ate um `ADMIN` aprova-las.
- `ADMIN` aprova ou rejeita propostas e cria usuarios `STAFF`.
- Deve existir ao menos um `ADMIN` provisionado diretamente na base de dados. Nenhuma request pode criar um usuario `ADMIN` ou escolher um papel.
- O cadastro de `USER` e livre; a criacao de `STAFF` e restrita a `ADMIN`.

## Tecnologias

- Java 21
- Maven, pelo Maven Wrapper incluido no projeto
- Spring Boot 4.1.1
- Spring Web MVC, para criar endpoints HTTP
- Spring Boot Actuator, para recursos de monitoramento
- Spring Web MVC Test, para testes da camada web
- Spring Data JPA, para persistencia com Hibernate
- Spring Security, para autenticacao e autorizacao (autenticacao basica implementada; papeis do catalogo pendentes)
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

As credenciais sao lidas de variaveis locais e nao devem ser adicionadas ao repositorio. Como `ddl-auto=none`, alteracoes no schema MySQL precisam ser feitas manualmente. A intencao e adotar Liquibase futuramente para versionar e aplicar as mudancas de schema; ele ainda nao esta configurado.

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
3. O usuario criado por `/signup` nao recebe papel atualmente. Nao o promova a `ADMIN`: mantenha um `ADMIN` provisionado diretamente na base e use essa conta para testar `/users`. A futura implementacao deve atribuir `USER` no cadastro.
4. `GET /users` com **Basic Auth** da conta `ADMIN` preexistente (e-mail e senha). O CSRF so e exigido em metodos que alteram dados, entao o header e desnecessario no `GET`.

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

### Roteiro para o catalogo e a base de dados

O banco e gerido manualmente (`ddl-auto=none`) por enquanto. A intencao e adotar Liquibase futuramente para versionar e aplicar as mudancas de schema. Ate essa adocao, antes de alterar tabelas, conferir o schema real com `SHOW CREATE TABLE`, identificar dados existentes e fazer backup. Registrar cada alteracao em script SQL versionado e aplica-lo de forma controlada; nao habilitar atualizacao automatica do Hibernate.

1. **Corrigir e completar o schema:** garantir `products.name`, `products.description`, `products.price` com tipo decimal adequado a dinheiro e `products.id_category`; criar `categories` se ainda nao existir. Adicionar chaves estrangeiras para categorias. Confirmar `users.email` como `UNIQUE` e `users.password_hash` existente.
2. **Fortalecer papeis e associacoes:** inserir exatamente `USER`, `STAFF` e `ADMIN` em `roles` (sem prefixo `ROLE_`); garantir `UNIQUE` em `roles.name`, chaves estrangeiras em `user_roles` para `users` e `roles`, e unicidade do par usuario/papel. Conferir dados existentes antes de adicionar constraints. Provisionar pelo menos um `ADMIN` diretamente no banco, com senha BCrypt; requests nao podem atribuir esse papel.
3. **Mapear o catalogo:** alinhar entidades e relacionamentos JPA com tabelas e constraints, incluindo `Category`; usar `BigDecimal` para valores monetarios. Adicionar DTOs validados para produtos e categorias, sem expor entidades diretamente.
4. **Aplicar papeis na API:** atribuir `USER` pelo servidor durante `/signup`; criar endpoint de criacao de `STAFF` acessivel somente a `ADMIN`. Nunca aceitar papel enviado pelo cliente. Restringir consulta do catalogo a usuarios autenticados com papel permitido e escritas a `STAFF` ou `ADMIN`, conforme regras acima.
5. **Criar fluxo de aprovacao:** adicionar tabela de propostas de alteracao ligada ao produto e ao `STAFF` autor, com valores propostos, estado (`PENDING`, `APPROVED`, `REJECTED`), revisor e datas. `STAFF` cria propostas; `ADMIN` aprova ou rejeita. Aplicar os valores aprovados ao catalogo em transacao; nunca atualizar produto publicado ao receber proposta.
6. **Testar autorizacao e integridade:** cobrir cadastro livre de `USER`, proibicao de papel enviado, criacao de `STAFF` somente por `ADMIN`, leitura por `USER`, proposta por `STAFF`, aprovacao/rejeicao por `ADMIN`, CSRF, validacoes, constraints e atualizacao atomica do catalogo.
7. **Preparar execucao segura:** resolver carregamento de `.env` nos testes e `spring-boot:run`, usar HTTPS em producao e avaliar limite de tentativas de login. Manter CSRF ativo para clientes navegador.

### Outras pendencias

- Adicionar tamanho minimo de senha (`@Size`) e DTO de resposta para `/signup`.
- Padronizar respostas de erro de validacao.
- Cobrir com MockMvc os casos de autenticacao existentes: `401` sem login, `403` sem papel e respostas de sucesso com papel autorizado.
- Consultar endpoints do Actuator, como `http://localhost:8080/actuator/health`.

### Front-end simples (futuro)

Criar uma aplicacao web simples para exercitar o fluxo pelo navegador:

- Telas de cadastro e login, e uma tela restrita que consome `/users` (visivel apenas para `ADMIN`).
- Obter o token em `/csrf` e envia-lo no header `X-CSRF-TOKEN` nas chamadas que alteram dados, enviando os cookies (`credentials: 'include'` em `fetch`).
- Definir a estrategia de autenticacao no navegador: HTTP Basic reenvia as credenciais a cada chamada e nao tem logout real; considerar migrar para login com sessao (`formLogin`/endpoint de login) ao implementar o front.
- Se o front rodar em outra origem (ex.: servidor de desenvolvimento em outra porta), configurar CORS de forma restrita, permitindo credenciais apenas para a origem conhecida, sem desativar o CSRF.