plugins {
    alias(libs.plugins.suac.feature.impl)
}

compose.resources {
    packageOfResClass = "org.momosi.shutupandcharge.feature.welcome.impl.resources"
    generateResClass = always
}

kotlin {
    android {
        namespace = "org.momosi.shutupandcharge.feature.welcome.impl"
    }
    sourceSets {
        commonMain.dependencies {
            api(projects.feature.welcome.api)
            implementation(projects.core.common)
            implementation(projects.core.user)
            implementation(projects.core.charger)
        }
    }
}
