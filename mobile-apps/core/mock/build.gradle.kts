plugins {
    alias(libs.plugins.suac.library)
    alias(libs.plugins.suac.test)
    alias(libs.plugins.suac.koin)
}

kotlin {
    android {
        namespace = "org.momosi.shutupandcharge.core.mock"
    }
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.datetime)
            implementation(projects.core.common)
            implementation(projects.core.user)
            implementation(projects.core.charger)
            implementation(projects.core.transaction)
            implementation(projects.core.account)
        }
    }
}
