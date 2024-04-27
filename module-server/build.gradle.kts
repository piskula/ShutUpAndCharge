plugins {
    id("org.springframework.boot") version "3.1.4"
    kotlin("plugin.jpa") version "1.9.23"
}

val uiProject = project(":module-ui")

springBoot {
  // enable versioning and build info to be injected
  buildInfo()
}

dependencies {
    implementation(project(":module-api"))
    implementation(uiProject) /* because of putting static sources into jar, can be removed */

    // controller
    implementation("org.springframework.boot:spring-boot-starter-web")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.mariadb.jdbc:mariadb-java-client:3.3.3")
    implementation("org.flywaydb:flyway-mysql:9.22.2")

    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-security")
    // validate JWT tokens from clients
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

    // make swagger documentation available
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
//    testImplementation("io.mockk:mockk:1.13.4")
}

val frontendDestination = "src/main/resources/public"
// extend cleanup also to angular build directory
tasks.clean {
    doFirst { delete(frontendDestination) }
}

val copyFrontend = tasks.create("copyFrontend", Copy::class) {
  from("${uiProject.layout.buildDirectory.asFile.get()}/generated-resources/static/browser")
  into(frontendDestination)
}

copyFrontend.dependsOn += tasks.getByPath(":module-ui:npmBuild")

tasks.processResources {
  dependsOn(copyFrontend)
}

//tasks.test {
//    useJUnitPlatform()
//}
