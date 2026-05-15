# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

ShutUpAndCharge is an EV charger access management web app deployed at https://charge.momosi.org/. It's a full-stack Kotlin + Angular monorepo with three Gradle modules.

## Build Commands

```bash
# Full build (compiles UI → copies to server → packages fat JAR)
./gradlew clean build

# Run locally (after build)
java -jar module-server/build/libs/module-server-*-SNAPSHOT.jar

# Start local PostgreSQL
docker-compose up -d

# Frontend dev (in module-ui/)
npm start            # Angular dev server
npm test             # Karma/Jasmine tests
npm run build        # Production build only
```

The Gradle build auto-generates TypeScript Angular API client from `module-api/api-docs.json` into `module-ui/build/generated-sources/module-api/` before compiling the UI. The compiled frontend is then copied into `module-server/src/main/resources/public/` so it is then part of built jar file and served automatically.

## Module Architecture

**`module-api`** — API contract layer.
- Defines ALL endpoint contracts: `@PostMapping`, `@Operation`, `@Tag`, produces/consumes, return types
- `module-server` controllers implement these interfaces — they add nothing to routing or docs on their own
- Also used to generate OpenAPI 3.1.0 spec file (`api-docs.json`)
  - `api-docs.json` is **hand-written** — it is NOT auto-generated from annotations. Changes to endpoints/DTOs require manually updating this file and rebuilding so the Angular client regenerates.
  - _in the future, building of api-docs.json file should also happen as part of the build_

**`module-server`** — Spring Boot 4 backend (Kotlin, Java 21). Organized into domain packages under `sk.momosilabs.suac.server`:
- `account/` — provides endpoints for managing user accounts
- `dashboard/` — provides data for homepage after login, user account balance, his last transactions
- `transaction/` — finished and temporary transaction endpoints and their scheduled pairing process
- `security/` — Security Config and public (non-auth) current user endpoint
- `info/` — public (non-auth) endpoints for providing version and charger status
- `common/` — shared config and utilities

**`module-ui`** — Angular 20 SPA. Uses Keycloak JS 26.2.4 for browser-side OIDC login. API calls use the generated TypeScript client. Angular Material for UI components. Organized into `authenticated/`, `non-authenticated/`, and `common/` directories.

## Backend Layering Rules

The architecture follows a strict three-layer pattern:

```
Controller → UseCase → Persistence (or shared Service)
```

- **Controllers** implement the `module-api` interface and inject use cases only. No persistence calls, no business logic.
- **Use cases** contain business logic. They call persistence or shared services (e.g. `CurrentUserService`, `ExcelExportService`). **Use cases never call other use cases.** If two use cases need the same query, both call persistence independently.
- **Persistence** handles all data access.

Each use case lives in its own package under `.service.<useCaseName>/` with two files:
- `<UseCaseName>UseCase.kt` — interface
- `<UseCaseName>.kt` — `@Service open class` implementing the interface, with `@IsUser`/`@IsAdmin` and `@Transactional` on the override method

## QueryDSL Notes

**`when` is a reserved keyword in Kotlin** — use backtick escaping with `CaseBuilder`:
```kotlin
CaseBuilder().`when`(transaction.price.lt(BigDecimal.ZERO)).then(1L).otherwise(0L).sum()
```

**`fetchResults()` does not work with GROUP BY** — for grouped queries, run two separate queries: one `fetch()` for content and one for the count (`.select(keys).groupBy(keys).fetch().size.toLong()`).

**Define aggregation expressions at class level** (not inside functions) so they can be referenced in ORDER BY without re-instantiation.

## Security Config Note

`anyRequest().permitAll()` in Spring Security is intentional — tightening it breaks Angular SPA routes (e.g. `/authenticated/home`) which are served as static files from the JAR. Actual endpoint security is enforced via `@IsUser`/`@IsAdmin` on use case methods. This will be revisited when the frontend is served from NGINX instead of the JAR.

## Key Configuration

**OAuth2/Keycloak:** Issuer URI is `https://sso.momosi.org/realms/momosi`. The backend is a resource server only — it validates JWTs but does not redirect for login.

**Database:** PostgreSQL with Flyway migrations. JPA is set to `ddl-auto: validate` (schema managed entirely by Flyway). `out-of-order: true` is set on Flyway.

**External charger API:** go-e.io cloud API (status/set/download endpoints) — tokens are in `application.yml`.

**Local environment variables needed:**
- `POSTGRES_DB_URL` — defaults to `jdbc:postgresql://localhost:5432/charging?stringtype=unspecified`
- `POSTGRES_DB_PASSWORD` — defaults to `charging-password`

- For using go-e external endpoints, these fields need to be provided:
```yaml
application:
  station:
    cloudStatusUrl: "[SECRET]"
    cloudSetUrl: "[SECRET]"
    cloudToken: "[SECRET]"
    cloudDownload: "https://data.v3.go-e.io/api/v1/direct_json"
    cloudDownloadToken: "[SECRET]"
```

## Runtime
Currently, application, when deployed on PROD, uses Docker on Ubuntu VM. In Docker there is keycloak, postgres and NGINX. Application itself is running as a service in Linux with `java -jar` command. There is 1 shared postgres with 2 schemas, 1 for keycloak and 1 for Spring Boot app.

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend language | Kotlin 2.2.21 + Java 21 |
| Framework | Spring Boot 4.0.6 |
| ORM / queries | Spring Data JPA + QueryDSL 5.1.0 |
| DB | PostgreSQL + Flyway migrations |
| Auth | Keycloak (OAuth2/OIDC), Spring Security OAuth2 Resource Server |
| API spec | OpenAPI 3.1.0 (handwritten `api-docs.json`) |
| Frontend | Angular 20, Angular Material, RxJS |
| Frontend auth | Keycloak JS 26.2.4 |
| Build | Gradle 9.4.0, Gradle Node Plugin (frontend builds via Gradle) |
| CI/CD | GitHub Actions → artifact upload → GCP VM |
