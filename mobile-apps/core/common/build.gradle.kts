plugins {
    alias(libs.plugins.suac.library)
    alias(libs.plugins.suac.koin)
    alias(libs.plugins.suac.firebase)
    alias(libs.plugins.suac.ktor)
}

kotlin {
    android {
        namespace = "org.momosi.shutupandcharge.core.common"
    }
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kermit)
            implementation(libs.kotlinx.datetime)
            api(projects.core.dto)
        }
        androidMain.dependencies {
            implementation(libs.androidx.appcompat)
        }
    }
}
