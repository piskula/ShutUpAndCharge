plugins {
    alias(libs.plugins.suac.feature.impl)
}

compose.resources {
    packageOfResClass = "org.momosi.shutupandcharge.feature.profile.impl.resources"
    generateResClass = always
}

kotlin {
    android {
        namespace = "org.momosi.shutupandcharge.feature.profile.impl"
    }
    sourceSets {
        commonMain.dependencies {
            api(projects.feature.profile.api)
            implementation(projects.core.common)
            implementation(projects.core.user)
        }
    }
}
