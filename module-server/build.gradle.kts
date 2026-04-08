import org.gradle.api.tasks.Copy
import org.gradle.jvm.tasks.Jar

plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("plugin.jpa")
    kotlin("plugin.serialization")
    id("org.jetbrains.kotlin.kapt")
}

val uiProject = project(":module-ui")

springBoot {
  buildInfo()
}

// do not build *plain.jar, only fat jar
tasks.named<Jar>("jar") {
    enabled = false
}

dependencies {
    implementation(project(":module-api"))
    implementation(uiProject) /* because of putting static sources into jar, can be removed */

    // controller
    implementation("org.springframework.boot:spring-boot-starter-web")

    // JSON serialization for external rest client calls
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.postgresql:postgresql:42.7.4")
    implementation("org.flywaydb:flyway-database-postgresql:10.20.1")
    implementation("com.querydsl:querydsl-jpa:5.1.0:jakarta")
    kapt("com.querydsl:querydsl-apt:5.1.0:jakarta")

    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-security")

    // make swagger documentation available
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.2")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
//    testImplementation("io.mockk:mockk:1.13.4")
}

val frontendDestination = layout.projectDirectory.dir("src/main/resources/public")
// extend cleanup also to angular build directory
tasks.clean {
    doFirst { delete(frontendDestination) }
}

// Copy Angular build output into Spring Boot static resources
val copyFrontend = tasks.register<Copy>("copyFrontend") {
    dependsOn(":module-ui:npmBuild")

    from(uiProject.layout.buildDirectory.dir("frontend-dist/browser"))
    into(frontendDestination)
}

// Ensure frontend is included in jar
tasks.processResources {
    dependsOn(copyFrontend)
}
