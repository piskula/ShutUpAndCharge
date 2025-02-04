import com.github.gradle.node.npm.task.NpmTask
import org.hidetake.gradle.swagger.generator.GenerateSwaggerCode

plugins {
  id("com.github.node-gradle.node") version "7.1.0"
  id("org.hidetake.swagger.generator") version "2.19.2"
}

// to make build on non-dev machines easier, use bundled Node
node {
    download.set(true)
    version = "18.20.4"
}

val buildFrontendBundle = configurations.create("build-frontend-bundle")

val apiProject = project(":module-api")

dependencies {
  compileOnly(apiProject)
  swaggerCodegen("io.swagger.codegen.v3:swagger-codegen-cli:3.0.67")

  buildFrontendBundle(files(npmBuild))
}

swaggerSources {
  create("frontendFromApi").apply {
    setInputFile(file("${apiProject.layout.buildDirectory.asFile.get()}/swagger/${apiProject.name}.json"))
    code(closureOf<GenerateSwaggerCode> {
      language = "typescript-angular"
      outputDir = file("${layout.buildDirectory.asFile.get()}/${apiProject.name}")
      templateDir = file("customSwaggerTemplate")
    })
  }
}

val generationTask = tasks.findByPath("generateSwaggerCodeFrontendFromApi")!!

generationTask.dependsOn += apiProject.tasks.findByPath("generateSwaggerDocumentation")

tasks.yarn {
  dependsOn(generationTask)
}

val npmBuild = tasks.create("npmBuild", NpmTask::class) {
    args.set(listOf("run", "build"))
    dependsOn(tasks.yarn)
}

// make swagger codegen and npm build part of build task
tasks.build {
    dependsOn(npmBuild)
}
