plugins {
    alias(libs.plugins.suac.library)
    alias(libs.plugins.suac.test)
    alias(libs.plugins.suac.koin)
    alias(libs.plugins.suac.ktor)
}

kotlin {
    android {
        namespace = "org.momosi.shutupandcharge.core.charger"
    }
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.common)
            implementation(projects.core.network)
            implementation(projects.core.dto)
        }
        commonTest.dependencies {
            implementation(libs.ktor.client.mock)
        }
    }
}
