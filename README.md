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
```

The database is shared across all microservices. Do not run Liquibase here.
One migration owner — this library's `liquibase` Maven profile, or a CI step
calling `liquibase:update` — applies changesets. Services only validate at boot
via `ddl-auto: validate`; a mismatch fails fast instead of racing on schema
version.

The microservice must scan the shared classes, because their packages differ
from the application package:

```java
@EntityScan("com.jerokysys.commons.domain")
@EnableJpaRepositories("com.jerokysys.commons.repository")
```

Component scanning must cover `com.jerokysys` (generated controllers live in
`com.jerokysys.controller`).

## Map DTOs to entities

`BaseService` requires `toEntity(D)` and `toDto(E)` for every resource. Implement
them in the microservice with MapStruct.

Add MapStruct to the microservice `pom.xml`:

```xml
<properties>
    <org.mapstruct.version>1.6.3</org.mapstruct.version>
</properties>

<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>${org.mapstruct.version}</version>
</dependency>
```

Configure the compiler annotation processors. Order matters:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <annotationProcessorPaths>
            <path>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
            </path>
            <path>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok-mapstruct-binding</artifactId>
                <version>0.2.0</version>
            </path>
            <path>
                <groupId>org.mapstruct</groupId>
                <artifactId>mapstruct-processor</artifactId>
                <version>${org.mapstruct.version}</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

Without `lombok-mapstruct-binding` MapStruct runs before Lombok has generated the
getters and setters, and every mapped field comes out null.

Write one abstract mapper per entity. Use an abstract class when the mapper must
resolve a relation (`guardianId` to `Guardian`):

```java
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class StudentMapper {

    @Autowired
    protected GuardianRepository guardianRepository;

    @Mapping(target = "guardianId", source = "guardian.id")
    public abstract StudentDTO toDto(Student entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "guardian", ignore = true)
    public abstract Student toEntity(StudentDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "guardian", ignore = true)
    public abstract void updateEntity(StudentDTO dto, @MappingTarget Student entity);

    @AfterMapping
    protected void linkGuardian(StudentDTO dto, @MappingTarget Student entity) {
        if (dto.getGuardianId() != null) {
            entity.setGuardian(guardianRepository.getReferenceById(dto.getGuardianId()));
        }
    }
}
```

`unmappedTargetPolicy = IGNORE` suppresses warnings for entity fields absent from
the DTO (`createdAt`, `active`, `deleted`).

Delegate from the service:

```java
@Override
public StudentDTO toDto(Student entity) {
    return mapper.toDto(entity);
}

@Override
public Student toEntity(StudentDTO dto) {
    return mapper.toEntity(dto);
}

@Override
public StudentDTO create(StudentDTO dto) {
    return mapper.toDto(repository.save(mapper.toEntity(dto)));
}

@Override
public StudentDTO update(Long id, StudentDTO dto) {
    Student entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("ENTITY_NOT_FOUND", "Entity not found with id: " + id));
    mapper.updateEntity(dto, entity);
    return mapper.toDto(repository.save(entity));
}
```

Rules:

- Map inside a transaction; lazy relations such as `guardian` fail outside one.
- `search` must add a specification `deleted = false`. `getById` and `getAll`
  do not filter soft-deleted rows.
- Ignore the DTO `id` on create; the database assigns it.

## Liquibase migrations

The shared database is migrated from this library only, never from a
microservice. Database commands run through the `liquibase` Maven profile. This
profile reads credentials from `src/main/resources/config/private.properties`.

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
