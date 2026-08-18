# Copilot Instructions for Book-Library-Service

## Project overview
Spring Boot web application for managing a book library (books, readers, borrow/return),
with both a Thymeleaf web UI and a REST API (`/api/v1/book/**`, `/api/v1/reader/**`). It's a Gradle
multi-module build: `app` (the Spring Boot service) and `cdk` (AWS CDK infrastructure-as-code, TypeScript).
`buildSrc` holds shared Gradle convention plugins.

## Build, test, and lint
Run all commands from the repository root using the Gradle wrapper (`./gradlew`, or `gradlew.bat` on Windows).

- Run the app locally with dev profile + Testcontainers-backed dependencies: `./gradlew :app:bootTestRun`
- Run the app with AWS profile: `./gradlew :app:bootRun`
- Run all tests: `./gradlew :app:test`
- Run a single test class: `./gradlew :app:test --tests "com.book.library.book.BookServiceTest"`
- Run a single test method: `./gradlew :app:test --tests "com.book.library.book.BookServiceTest.methodName"`
- Generate coverage report (also runs tests): `./gradlew :app:jacocoTestReport`
- Run SonarQube analysis (used in CI, needs `SONAR_TOKEN`): `./gradlew :app:sonar`
- Format code / apply formatting rules (Palantir Java Format, ktfmt for `*.gradle.kts`): `./gradlew spotlessApply`
- Check formatting only: `./gradlew spotlessCheck`
- Apply OpenRewrite recipes configured in `buildSrc/.../openrewrite-conventions.gradle.kts` (import order, unused imports, no-static-import, Spring Boot migration recipes): `./gradlew :app:rewriteRun` (dry-run: `rewriteDryRun`)
- Generate CycloneDX SBOM: `./gradlew :app:cyclonedxDirectBom`

CI (`.github/workflows/04-run-tests.yml`) runs `./gradlew :app:jacocoTestReport :app:sonar` on every push/PR — always ensure this passes before considering a change complete.

Tests use JUnit 6, Testcontainers (PostgreSQL, LocalStack for SQS/SES/DynamoDB, Keycloak for OAuth2/OIDC), and Awaitility for async assertions.
Test-only Spring context wiring lives in `ContainersConfig` (`app/src/test/java/com/book/library/config`), which spins up containers via `@ServiceConnection`/`DynamicPropertyRegistry` and provisions the SQS queue, DynamoDB table, and SES verified emails needed by tests.

## Architecture
- **Package layout by feature**, under `com.book.library`: `book`, `reader`, `recommendation`, `user`, `tracing`, `config`, `controller`, `dto`.
- Each feature typically has: JPA entity (e.g. `Book`), `*Repository` (Spring Data JPA), `*Service` (business logic), `*Controller` (Thymeleaf/MVC, package-private) and `*RestController` (JSON API), plus a `*ControllerAdvice` for exception→HTTP mapping and custom exceptions (e.g. `BookNotFoundException`, `BookDeletionException`).
- MVC and REST controllers for the same feature share logic via an `Abstract*Controller` base class (e.g. `AbstractBookController`, `AbstractReaderController`) that both `BookController` and `BookRestController` extend — put shared logging/orchestration there, not duplicated in both controllers.
- DTOs (`com.book.library.dto`) are the only types exposed over HTTP/UI; controllers convert to/from JPA entities, never leaking entities directly.
- **Security** (`config/SecurityConfig`): two separate `SecurityFilterChain`s — one stateless, JWT/OAuth2-resource-server-secured chain for `/api/v1/**` REST endpoints, and one stateful, session-based OAuth2 login chain (AWS Cognito / Keycloak in tests) for the web UI. Roles are extracted from the OIDC/JWT `resource_access` claim. Method-level security is enabled (`@EnableMethodSecurity`).
- **Book recommendations** (`recommendation` package) are event-driven: `BookRecommendationService` publishes to an SQS queue (`custom.recommendation-queue`), `BookRecommendationListener`/`DefaultBookRecommendationListener` consume it, and SES sends confirmation emails. LocalStack emulates SQS/SES/DynamoDB in tests.
- **Tracing** (`tracing` package): `TraceDao`/`Breadcrumb`/`TracingEvent` persist request breadcrumbs to a DynamoDB table (`custom.tracing-table`).
- Flyway manages the PostgreSQL schema (`app/src/main/resources/db`); `ddl-auto` is `none` — schema changes must go through Flyway migrations, not Hibernate auto-DDL.
- `AppConfig`/`CustomConfigurationProperties` bind the `custom.*` keys in `application.yml` (invitation codes, queue/table names, feature toggles like `use-real-sqs-listener`, `auto-confirm-recommendations`).
- `cdk/` (separate Node/TypeScript project, `cdk.json` + `package.json`) defines the AWS infrastructure (Cognito, DynamoDB, SQS, SES, ECS/deployment) that the `app` module runs against in production; it is built/deployed independently of the Gradle `app` build.

## Key conventions
- Controllers, DTOs, and internal classes are package-private by default (no `public` modifier) unless they need to be accessed from another package — follow this visibility pattern for new feature classes.
- Bean/constructor null-checks use `Objects.requireNonNull(...)`.
- Unused lambda/catch parameters are named `_` (Java 21+ unnamed variables), e.g. `catch (DataIntegrityViolationException | InvalidDataAccessApiUsageException _)`.
- String formatting uses `"...".formatted(...)` rather than `String.format`.
- Import order, unused-import removal, and no-static-imports are enforced via Spotless + OpenRewrite (`buildSrc/src/main/kotlin/*-conventions.gradle.kts`) — run `spotlessApply`/`rewriteRun` after larger edits instead of hand-formatting.
- Custom bean validation annotations live alongside their validators in `user` (`@ValidPassword`/`PasswordValidator`, `@ValidInvitationCode`/`InvitationCodeValidator`).
- Test helper base classes: `AbstractTestResources`, `AbstractControllerTest`, `AbstractTestRepository`/`BaseTestRepository` centralize shared setup — extend these instead of duplicating Testcontainers/MockMvc boilerplate in new tests.
