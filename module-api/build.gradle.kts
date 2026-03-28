plugins {
    id("org.springframework.boot") version "3.5.0" apply false
}

dependencies {
    // rest API annotations
    implementation("org.springframework.boot:spring-boot-starter-web")
    // SpringDoc OpenAPI for generating API documentation
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.16")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

dependencyManagement {
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
    }
}
