plugins {
    alias(libs.plugins.suac.feature.impl)
    alias(libs.plugins.suac.test)
}

compose.resources {
    packageOfResClass = "org.momosi.shutupandcharge.feature.admin.impl.resources"
    generateResClass = always
}

kotlin {
    android {
        namespace = "org.momosi.shutupandcharge.feature.admin.impl"
    }
    sourceSets {
        commonMain.dependencies {
            api(projects.feature.admin.api)
            implementation(projects.core.common)
            implementation(projects.core.account)
            implementation(projects.core.transaction)
            implementation(libs.kotlinx.collections.immutable)
        }
    }
}
