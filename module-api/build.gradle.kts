plugins {
    kotlin("jvm")
    id("io.spring.dependency-management")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("io.swagger.core.v3:swagger-annotations:2.2.52")
    implementation("org.springdoc:springdoc-openapi-starter-common:3.0.3")
    // to inject jwt into endpoint function
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.security:spring-security-oauth2-jose")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:4.1.0")
    }
}
