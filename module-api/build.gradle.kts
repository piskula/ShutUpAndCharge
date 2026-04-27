plugins {
    kotlin("jvm")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web:4.0.6")
    implementation("io.swagger.core.v3:swagger-annotations:2.2.22")
    implementation("org.springdoc:springdoc-openapi-starter-common:3.0.2")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
