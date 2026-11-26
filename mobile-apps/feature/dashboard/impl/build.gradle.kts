plugins {
    alias(libs.plugins.suac.feature.impl)
}

compose.resources {
    packageOfResClass = "org.momosi.shutupandcharge.feature.dashboard.impl.resources"
    generateResClass = always
}

kotlin {
    android {
        namespace = "org.momosi.shutupandcharge.feature.dashboard.impl"
    }
    sourceSets {
        commonMain.dependencies {
            api(projects.feature.dashboard.api)
            implementation(projects.feature.transactions.api)
            implementation(projects.core.common)
            implementation(projects.core.user)
            implementation(projects.core.charger)
            implementation(projects.core.account)
            implementation(projects.core.transaction)
            implementation(libs.kotlinx.collections.immutable)
        }
    }
}
