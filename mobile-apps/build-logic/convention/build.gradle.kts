plugins {
    `kotlin-dsl`
}

group = "org.momosi.shutupandcharge.buildlogic"

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(libs.android.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.compose.multiplatform.gradle.plugin)
    implementation(libs.spotless.gradle.plugin)
    implementation(libs.ksp.gradle.plugin)
    implementation(libs.ktorfit.gradle.plugin)
    implementation(project(":detekt"))
}

gradlePlugin {
    plugins {
        register("suac.library") {
            id = "suac.library"
            implementationClass = "org.momosi.shutupandcharge.LibraryPlugin"
        }
        register("suac.test") {
            id = "suac.test"
            implementationClass = "org.momosi.shutupandcharge.TestPlugin"
        }
        register("suac.feature.api") {
            id = "suac.feature.api"
            implementationClass = "org.momosi.shutupandcharge.FeatureApiPlugin"
        }
        register("suac.feature.impl") {
            id = "suac.feature.impl"
            implementationClass = "org.momosi.shutupandcharge.FeatureImplPlugin"
        }
        register("suac.koin") {
            id = "suac.koin"
            implementationClass = "org.momosi.shutupandcharge.KoinPlugin"
        }
        register("suac.spotless") {
            id = "suac.spotless"
            implementationClass = "org.momosi.shutupandcharge.SpotlessPlugin"
        }
        register("suac.compose") {
            id = "suac.compose"
            implementationClass = "org.momosi.shutupandcharge.ComposePlugin"
        }
        register("suac.ktor") {
            id = "suac.ktor"
            implementationClass = "org.momosi.shutupandcharge.KtorPlugin"
        }
        register("suac.android.signing") {
            id = "suac.android.signing"
            implementationClass = "org.momosi.shutupandcharge.AndroidSigningPlugin"
        }
        register("suac.firebase") {
            id = "suac.firebase"
            implementationClass = "org.momosi.shutupandcharge.FirebasePlugin"
        }
    }
}
