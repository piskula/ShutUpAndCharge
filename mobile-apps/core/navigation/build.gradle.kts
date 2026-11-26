plugins {
    alias(libs.plugins.suac.library)
    alias(libs.plugins.suac.compose)
    alias(libs.plugins.suac.test)
}

kotlin {
    android {
        namespace = "org.momosi.shutupandcharge.core.navigation"
    }
}
