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
|  |  |  `- PasswordEncoderConfig.java
|  |  |- controller/
|  |  |  |- HelloController.java
|  |  |  |- ProductController.java
|  |  |  `- UserController.java
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

## Proximos experimentos

- Criar controllers para `Role` e `UserRoles` (ainda so existem os repositories).
- Adicionar propriedades ao `application.properties`, como `server.port`.
- Criar testes para endpoints HTTP.
- Consultar endpoints do Actuator, como `http://localhost:8080/actuator/health`.

## Autenticacao e autorizacao (em andamento)

Ja estao implementados:

- `spring-boot-starter-security` como dependencia.
- Um bean `PasswordEncoder` baseado em BCrypt.
- `User.passwordHash`, com o getter ignorado pelo Jackson.
- Busca de usuario por e-mail e carregamento de authorities pelo `DatabaseUserDetailsService`. Os nomes no banco sao convertidos para authorities com prefixo `ROLE_`.

O proximo passo e criar uma `SecurityFilterChain` com HTTP Basic e regras iniciais para os endpoints: `/hello` publico, endpoints de produtos autenticados e `/users` restrito a `ADMIN`. A configuracao ainda nao existe; portanto, as authorities sao carregadas, mas ainda nao ha regras proprias de autorizacao por endpoint.

Antes de testar com o banco, confirme que a coluna `password_hash` existe (o Hibernate esta configurado com `ddl-auto=none`) e que ha usuarios com hashes BCrypt e papeis associados em `user_roles`. Depois da cadeia de seguranca, validar respostas `401` (nao autenticado) e `403` (sem permissao) e decidir a politica de CSRF para os clientes da API.

Validacao de entrada (`@NotBlank`, `@Email` etc.) continua como etapa posterior, preferencialmente aplicada a DTOs de requisicao.
