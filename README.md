# Spring Boot Demo

Projeto base para experimentos e aprendizado com Spring Boot. Use este repositório para criar endpoints, explorar configuracoes, escrever testes e entender o ciclo de desenvolvimento de uma aplicacao web Java.

> **Sobre o uso de IA neste projeto:** este é um projeto de estudo. A maior parte do código é escrita e pensada pelo autor, estudando os padrões corretos antes de aplicá-los. A IA é usada como apoio pontual — para validar decisões, explicar erros e sugerir boas práticas — não para gerar as funcionalidades do zero. Veja [CLAUDE.md](CLAUDE.md) para as diretrizes completas de como a IA deve colaborar aqui.

## Tecnologias

- Java 21
- Maven, pelo Maven Wrapper incluido no projeto
- Spring Boot 4.1.1
- Spring Web MVC, para criar endpoints HTTP
- Spring Boot Actuator, para recursos de monitoramento
- Spring Web MVC Test, para testes da camada web
- Spring Data JPA, para persistencia com Hibernate
- Spring Boot Validation, para validar dados de entrada
- MySQL Connector/J (`com.mysql:mysql-connector-j`), driver de conexao com MySQL

## Estrutura

```text
src/
|- main/
|  |- java/com/example/demo/
|  |  |- DemoApplication.java
|  |  |- controller/
|  |  |  |- HelloController.java
|  |  |  |- ProductController.java
|  |  |  `- UserController.java
|  |  |- model/
|  |  |  |- Product.java
|  |  |  |- User.java
|  |  |  |- Role.java
|  |  |  `- UserRoles.java
|  |  `- repository/
|  |     |- ProductRepository.java
|  |     |- UserRepository.java
|  |     |- RoleRepository.java
|  |     `- UserRolesRepository.java
|  `- resources/
|     `- application.properties
`- test/
   `- java/com/example/demo/
      `- DemoApplicationTests.java
```

A classe `DemoApplication` e o ponto de entrada da aplicacao. Controllers, models e repositories ficam separados por camada dentro de `com.example.demo`, para serem encontrados automaticamente pelo Spring Boot.

## Modelo de dados atual

- `Product`: produtos, vinculados a uma categoria por `idCategory` (sem relacionamento JPA ainda, apenas o id bruto).
- `User`: usuarios da aplicacao (`name`, `email`).
- `Role`: papeis/perfis (`name`), usados para controle de acesso.
- `UserRoles`: tabela de associacao entre `User` e `Role` (`idUser`, `idRole`), tambem sem relacionamento JPA ainda.

Os campos das entidades seguem `camelCase` (ex.: `idUser`, `idCategory`), e o Hibernate converte automaticamente para `snake_case` nas colunas do banco (`id_user`, `id_category`) via `PhysicalNamingStrategy` padrao do Spring Boot.

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

## Proximos experimentos

- Criar controllers para `Role` e `UserRoles` (ainda so existem os repositories).
- Adicionar propriedades ao `application.properties`, como `server.port`.
- Criar testes para endpoints HTTP.
- Consultar endpoints do Actuator, como `http://localhost:8080/actuator/health`.

### Proxima etapa planejada: autenticacao e validacao

Com `User`, `Role` e `UserRoles` no lugar, o proximo passo do estudo e implementar autenticacao e validacao de cada requisicao, usando esses papeis para autorizacao. Ideias para explorar:

- Adicionar `spring-boot-starter-security` e configurar autenticacao (ex.: login com usuario/senha ou JWT).
- Usar `UserRoles` para autorizar endpoints por papel (`@PreAuthorize`, `hasRole(...)`).
- Adicionar validacao de entrada (`@NotBlank`, `@Email`, etc.) nos DTOs/entidades antes de persistir dados vindos de requisicoes.
