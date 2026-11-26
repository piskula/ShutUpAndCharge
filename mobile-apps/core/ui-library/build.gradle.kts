plugins {
    alias(libs.plugins.suac.library)
    alias(libs.plugins.suac.compose)
}

compose.resources {
    packageOfResClass = "org.momosi.shutupandcharge.core.uilibrary.resources"
    generateResClass = always
}

kotlin {
    android {
        namespace = "org.momosi.shutupandcharge.core.uilibrary"
    }
    sourceSets {
        commonMain.dependencies {
            api(libs.kotlinx.collections.immutable)
            implementation(projects.core.common)
            api(projects.core.charger)
            api(projects.core.transaction)
        }
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
        }
    }
}
