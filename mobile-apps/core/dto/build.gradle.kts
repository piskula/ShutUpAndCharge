plugins {
    alias(libs.plugins.suac.library)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    android {
        namespace = "org.momosi.shutupandcharge.core.dto"
    }
    sourceSets {
        commonMain.dependencies {
            api(libs.kotlinx.serialization.json)
        }
    }
}
