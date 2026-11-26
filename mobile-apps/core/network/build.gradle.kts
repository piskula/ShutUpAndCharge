plugins {
    alias(libs.plugins.suac.library)
    alias(libs.plugins.suac.test)
    alias(libs.plugins.suac.koin)
    alias(libs.plugins.suac.ktor)
}

kotlin {
    android {
        namespace = "org.momosi.shutupandcharge.core.network"
    }
    sourceSets {
        commonMain.dependencies {
            api(projects.core.dto)
            implementation(projects.core.common)
            implementation(projects.core.user)
        }
        commonTest.dependencies {
            implementation(libs.ktor.client.mock)
        }
    }
}
