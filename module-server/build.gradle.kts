import org.gradle.api.tasks.Copy
import org.gradle.jvm.tasks.Jar

plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    kotlin("plugin.serialization")
    id("org.jetbrains.kotlin.kapt")
}

val uiProject = project(":module-ui")
val querydslVersion = "7.7"

// prevent the legacy com.querydsl group (replaced by io.github.openfeign.querydsl) from sneaking in
configurations.all {
    exclude(group = "com.querydsl")
}

springBoot {
  buildInfo()
}

// do not build *plain.jar, only fat jar
tasks.named<Jar>("jar") {
    enabled = false
}

dependencies {
    implementation(project(":module-api"))
    implementation(project(":module-ui")) /* because of putting static sources into jar, can be removed */

    // controller
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("tools.jackson.module:jackson-module-kotlin")

    // JSON serialization for external rest client calls
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.postgresql:postgresql")
    implementation("org.springframework.boot:spring-boot-starter-flyway")
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("io.github.openfeign.querydsl:querydsl-jpa:$querydslVersion")
    kapt("io.github.openfeign.querydsl:querydsl-apt:$querydslVersion:jpa")

    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-security")

    // make swagger documentation available
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.2.0")

    implementation("org.apache.poi:poi-ooxml:5.5.1")

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
