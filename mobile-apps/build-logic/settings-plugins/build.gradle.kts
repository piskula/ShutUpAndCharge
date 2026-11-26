plugins {
    `kotlin-dsl`
}

group = "org.momosi.shutupandcharge.buildlogic"

kotlin {
    jvmToolchain(21)
}

gradlePlugin {
    plugins {
        register("suac.git-hooks") {
            id = "suac.git-hooks"
            implementationClass = "org.momosi.shutupandcharge.GitHooksPlugin"
        }
    }
}
