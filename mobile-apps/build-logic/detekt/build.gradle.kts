plugins {
    `kotlin-dsl`
}

group = "org.momosi.shutupandcharge.buildlogic"

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(libs.detekt.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("suac.detekt") {
            id = "suac.detekt"
            implementationClass = "org.momosi.shutupandcharge.DetektPlugin"
        }
    }
}
