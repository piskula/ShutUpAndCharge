# Trucker File Manager

## Composition
This Application consists of:
- Server (Kotlin + Springframework)
- Keycloak (auth provider)
- MariaDB (data storage)
- MinIO (file storage)

## Code structure
This code is structured in 3 main modules:
- [module-api](module-api)
  - contains all API specifications, DTOs
- [module-server](module-server)
  - contains business logic (_"implements module-api"_)
- [module-ui](module-ui)
  - contains Angular SPA (FrontEnd code)
  - is re-using module-api endpoints and DTOs (automatically injected during build)

## Runtime configuration
Application require Keycloak and MariaDB to successfully start. Therefore, use [docker-compose](docker-compose.yml) file to start them.

### Keycloak
1. change default credentials
2. optional: you might need to disable HTTPS to login as admin 
   1. `docker exec -it keycloak /bin/bash`
   2. `cd /opt/keycloak/bin/`
   3. `./kcadm.sh config credentials --server http://localhost:8080 --realm master --user admin`
   4. `./kcadm.sh update realms/master -s sslRequired=NONE --server http://localhost:8080`
   5. restart
3. login with default admin credentials
4. create a new Realm
5. inside new Realm we will create new Client
   1. specify valid redirect URIs
   2. specify client scope `microprofile-jwt` to type `Default`
6. create Realm Role `user`
7. optional: you can create default test users
   1. add this role to them!
   2. set credential to them!
8. optional: you can set up additional identity providers like Google
   1. just add them and specify client id+secret
9. inside [application.yml](module-server/src/main/resources/application.yml) change `client-id`, `issuer-uri` or other setup if needed

### Server
1. trigger the complete build by running:
    - `./gradlew clean build`
2. inside [libs](module-server/build/libs) there will be a jar file located
    - `java -jar [FILE]`
