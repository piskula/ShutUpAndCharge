plugins {
    alias(libs.plugins.suac.library)
    alias(libs.plugins.suac.test)
    alias(libs.plugins.suac.koin)
    alias(libs.plugins.suac.ktor)
}

kotlin {
    android {
        namespace = "org.momosi.shutupandcharge.core.transaction"
        androidResources {
            enable = true
        }
    }
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.common)
            implementation(projects.core.network)
            implementation(projects.core.dto)
        }
        androidMain.dependencies {
            implementation(libs.androidx.appcompat)
        }
    }
}
