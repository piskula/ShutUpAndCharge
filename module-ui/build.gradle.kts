import com.github.gradle.node.npm.task.NpmTask

plugins {
  id("com.github.node-gradle.node") version "7.1.0"
  id("org.openapi.generator") version "7.21.0"
}

// to make build on non-dev machines easier, use bundled Node
node {
    download.set(true)
    version = "22.16.0"
}

val buildFrontendBundle = configurations.create("build-frontend-bundle")

val apiProject = project(":module-api")

dependencies {
  compileOnly(apiProject)

  buildFrontendBundle(files(npmBuild))
}

openApiGenerate {
    generatorName.set("typescript-angular")
    typeMappings.set(mapOf("DateTime" to "Date"))
    inputSpec.set("${apiProject.layout.projectDirectory}/api-docs.json")
    outputDir.set(layout.buildDirectory.dir("generated-sources/${apiProject.name}").map { it.asFile.absolutePath })
    apiPackage.set("api")
    modelPackage.set("model")
    configOptions.put("npmName", "my-api-client") // Optional: Name for the npm package
    configOptions.put("npmVersion", "1.0.0")     // Optional: Version for the npm package
}

val generationTask = tasks.findByPath("openApiGenerate")!!

// TODO module-api is currently not generating OpenAPI
//generationTask.dependsOn(apiProject.tasks.findByPath("generateOpenApiDocs")!!)

tasks.yarn {
  dependsOn(generationTask)
}

val npmBuild = tasks.register<NpmTask>("npmBuild") {
    args.set(listOf("run", "build"))
    dependsOn(tasks.yarn)
}

// make swagger codegen and npm build part of build task
tasks.build {
    dependsOn(npmBuild)
}
