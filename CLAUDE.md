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

**`module-api`** — API contract layer. See `module-api/CLAUDE.md` for details.

**`module-server`** — Spring Boot 4 backend (Kotlin, Java 21). See `module-server/CLAUDE.md` for details.

**`module-ui`** — Angular 21 SPA. See `module-ui/CLAUDE.md` for details.

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
| Frontend | Angular 21, Angular Material, RxJS |
| Frontend auth | Keycloak JS 26.2.4 |
| Build | Gradle 9.4.0, Gradle Node Plugin (frontend builds via Gradle) |
| CI/CD | GitHub Actions → artifact upload → GCP VM |
