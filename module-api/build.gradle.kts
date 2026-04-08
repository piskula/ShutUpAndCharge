plugins {
    id("org.springframework.boot") version "4.0.5" apply false
    kotlin("jvm") version "2.2.0"
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web:4.0.5")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.16")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
