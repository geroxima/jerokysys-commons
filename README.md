# JerokySys Commons
1. Overview
- Spring Boot commons project
- JPA + PostgreSQL + Liquibase + OpenAPI
2. Prerequisites
- Java 17+
- Maven
- PostgreSQL database
3. Setup
- Copy private.example.properties to private.properties
- Fill in DB credentials

```properties
db.url=jdbc:postgresql://web
db.username=db_user
db.password=db_passowrd
```

4. Commands
   
Command	Purpose

```bash
mvn spring-boot:run # Runs the app
```

```bash
mvn initialize liquibase:diff	# Diff DB against changelog
```

5. Development Workflow

- Phase 1 (initial): Hibernate creates tables, then generate changelog
- Phase 2 (ongoing): Liquibase validates, use diff for new changes

6. OpenAPI
- Spec at src/main/resources/openapi.yaml
- Generates server stubs to target/generated-sources/openapi