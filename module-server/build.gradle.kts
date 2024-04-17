plugins {
    id("org.springframework.boot") version "3.1.4"
}

val uiProject = project(":module-ui")

dependencies {
    implementation(project(":module-api"))
    implementation(uiProject) /* because of putting static sources into jar, can be removed */

    // controller
    implementation("org.springframework.boot:spring-boot-starter-web")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("mysql:mysql-connector-java:8.0.33")

    // make swagger documentation available
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
//    testImplementation("io.mockk:mockk:1.13.4")
}

val frontendDestination = "src/main/resources/static"
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
