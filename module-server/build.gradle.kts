plugins {
    id("org.springframework.boot") version "3.3.5"
    kotlin("plugin.jpa") version "2.1.0"
    kotlin("plugin.serialization") version "2.1.0"
    kotlin("kapt")
}

val uiProject = project(":module-ui")

springBoot {
  // enable versioning and build info to be injected
  buildInfo()
}

// do not build *plain.jar, only fat jar
tasks.getByName<Jar>("jar") {
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
  from("${uiProject.layout.buildDirectory.asFile.get()}/../dist/shut-up-and-charge/browser")
  into(frontendDestination)
}

copyFrontend.dependsOn += tasks.getByPath(":module-ui:npmBuild")

tasks.processResources {
  dependsOn(copyFrontend)
}

//tasks.test {
//    useJUnitPlatform()
//}
