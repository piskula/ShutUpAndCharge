# Shut Up And Charge

This is a web application, which purpose is to manage access to publicly accessible EV Charger.

## Composition
This Application consists of:
- Server (Kotlin + Springframework)
- Keycloak (auth provider)
- Data storage - currently Postgres

### Code structure
This code is structured in 3 main modules:
- [module-api](module-api)
  - contains all API specifications, DTOs
- [module-server](module-server)
  - contains business logic (_"implements module-api"_)
- [module-ui](module-ui)
  - contains Angular SPA (FrontEnd code)
  - is re-using module-api endpoints and DTOs (automatically injected during build)

## How to build
1. trigger the complete build by running:
   - `./gradlew clean build`
2. inside [build/libs](module-server/build/libs) there will be a jar file located
   - `java -jar [FILE]`

## Runtime configuration
Application itself requires Keycloak and PostgreDB to successfully start. Therefore, you can use
- for PostgreDB [docker-compose](docker-compose.yml) file to start local DB instance
- for Identity porivder you can use any existing keycloak instance, or you can also run your local instance in docker
  - whatever you will do, you only need to properly specify it in
[application.yml](module-server/src/main/resources/application.yml) file, namely client id, secret, and issuer uri
```yaml
    oauth2:
      client:
        registration:
          keycloak:
            client-id: "YOUR_VALUE"
            client-secret: "YOUR_VALUE"
        provider:
          keycloak:
            issuer-uri: "https://keycloak.public/realms/myRealm"
```

## Deployment
For deployment follow instructions in [devOps](devOps) folder.
