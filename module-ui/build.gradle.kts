import com.github.gradle.node.npm.task.NpmTask

plugins {
  id("com.github.node-gradle.node") version "7.1.0"
  id("org.openapi.generator") version "7.21.0"
}

node {
    download.set(true)
    version = "22.16.0"
}

val apiProject = project(":module-api")

openApiGenerate {
    generatorName.set("typescript-angular")
    typeMappings.set(mapOf("DateTime" to "Date"))
    inputSpec.set("${apiProject.layout.projectDirectory}/api-docs.json")
    outputDir.set(
        layout.buildDirectory
            .dir("generated-sources/${apiProject.name}")
            .map { it.asFile.absolutePath }
    )

    apiPackage.set("api")
    modelPackage.set("model")
    configOptions.put("npmName", "my-api-client") // Optional: Name for the npm package
    configOptions.put("npmVersion", "1.0.0")     // Optional: Version for the npm package
}

val npmBuild = tasks.register<NpmTask>("npmBuild") {
    dependsOn(tasks.yarn)
    dependsOn(tasks.openApiGenerate)

    args.set(listOf("run", "build"))

    outputs.dir(layout.buildDirectory.dir("frontend-dist"))
}

tasks.build {
    dependsOn(npmBuild)
}
