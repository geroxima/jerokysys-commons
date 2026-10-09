# JerokySys Commons

Shared library for JerokySys microservices.

- Spring Boot 4.1.1, Java 17
- JPA + PostgreSQL + Liquibase + OpenAPI
- Ships base entities, repositories, services, DTOs, and the Liquibase changelog
- Ships no database configuration or credentials; the consuming microservice owns them

## Prerequisites

- Java 17+
- Maven (use the wrapper: `./mvnw`)

## Build and install

Install into the local Maven repository (`~/.m2`):

```bash
./mvnw clean install
```

Artifact produced:

```
com.jerokysys:commons:0.0.3-SNAPSHOT
```

Verify the jar excludes application config and secrets:

```bash
unzip -l target/commons-0.0.3-SNAPSHOT.jar | grep -E "application.properties|private"
```

Expected output: empty.

## Use from a microservice

Add the dependency:

```xml
<dependency>
    <groupId>com.jerokysys</groupId>
    <artifactId>commons</artifactId>
    <version>0.0.3-SNAPSHOT</version>
</dependency>
```

The microservice provides its own database credentials and configuration in
`application.yml` or `application.properties`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://<host>/<database>?sslmode=require
    username: ${DB_USER}
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: validate
  liquibase:
    enabled: true
    change-log: classpath:db/changelog/db.changelog-master.yaml
```

The microservice must scan the shared classes, because their packages differ
from the application package:

```java
@EntityScan("com.jerokysys.commons.domain")
@EnableJpaRepositories("com.jerokysys.commons.repository")
```

Component scanning must cover `com.jerokysys` (generated controllers live in
`com.jerokysys.controller`).

## Liquibase migrations

Database commands run through the `liquibase` Maven profile. This profile reads
credentials from `src/main/resources/config/private.properties`.

Setup:

```bash
cp src/main/resources/config/private.example.properties src/main/resources/config/private.properties
```

Fill in `db.url`, `db.username`, and `db.password`.

Apply pending changesets:

```bash
./mvnw -Pliquibase process-resources liquibase:update
```

Show migration status:

```bash
./mvnw -Pliquibase process-resources liquibase:status
```

Generate a diff against the changelog:

```bash
./mvnw -Pliquibase process-resources liquibase:diff
```

`private.properties` is gitignored and never packaged into the jar. The plain
build (`./mvnw clean install`) does not read it and works without it.

## Repository layout

```
src/main/java/com/jerokysys/commons/
    common/       shared response and paging types
    controller/   base controller
    domain/       base entities, entities, enums
    exception/    exceptions and global handler
    repository/   base repository
    service/      base service and abstract implementation
src/main/resources/
    db/changelog/db.changelog-master.yaml   Liquibase changelog
    openapi.yaml                            OpenAPI spec
```

## OpenAPI

- Spec at `src/main/resources/openapi.yaml`
- Server stubs are generated to `target/generated-sources/openapi` during the
  build.
