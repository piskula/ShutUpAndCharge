plugins {
    alias(libs.plugins.suac.library)
    alias(libs.plugins.suac.koin)
    alias(libs.plugins.suac.ktor)
}

kotlin {
    android {
        namespace = "org.momosi.shutupandcharge.core.user"
    }
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.common)
            implementation(libs.oidc.core)
            api(libs.oidc.appsupport)
        }
    }
}
