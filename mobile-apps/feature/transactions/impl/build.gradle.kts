plugins {
    alias(libs.plugins.suac.feature.impl)
    alias(libs.plugins.suac.test)
}

compose.resources {
    packageOfResClass = "org.momosi.shutupandcharge.feature.transactions.impl.resources"
    generateResClass = always
}

kotlin {
    android {
        namespace = "org.momosi.shutupandcharge.feature.transactions.impl"
    }
    sourceSets {
        commonMain.dependencies {
            api(projects.feature.transactions.api)
            implementation(projects.core.common)
            implementation(projects.core.user)
            implementation(projects.core.transaction)
            implementation(libs.kotlinx.collections.immutable)
        }
    }
}
