import com.benjaminsproule.swagger.gradleplugin.model.ApiSourceExtension
import com.benjaminsproule.swagger.gradleplugin.model.InfoExtension

plugins {
    id("org.springframework.boot") version "3.5.0" apply false
    // TODO from 1.0.8 required flags are broken https://github.com/gigaSproule/swagger-gradle-plugin/issues/186 but if
    // lowered to 1.0.8 fasterxml lib is failing build
    id("com.benjaminsproule.swagger") version "1.0.14"
}

swagger {
    apiSource(closureOf<ApiSourceExtension> {
        val infoExtension = InfoExtension(project)
        infoExtension.title = project.name
        infoExtension.version = project.version.toString()

        springmvc = true
        outputFormats = listOf("json", "yaml")
        locations = mutableListOf("sk.momosilabs.suac.api")
        swaggerDirectory = "$buildDir/swagger"
        swaggerFileName = project.name
        info = infoExtension
        attachSwaggerArtifact = true
    })
}

dependencies {
    // rest API annotations
    implementation("org.springframework.boot:spring-boot-starter-web")
    // swagger annotations
    implementation("io.swagger:swagger-annotations:1.6.16")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

dependencyManagement {
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
    }
}

// make swagger generation part of build task
tasks.build {
	dependsOn(tasks.generateSwaggerDocumentation)
}
