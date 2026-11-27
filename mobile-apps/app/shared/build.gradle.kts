plugins {
    alias(libs.plugins.suac.library)
    alias(libs.plugins.suac.test)
    alias(libs.plugins.suac.compose)
    alias(libs.plugins.suac.koin)
}

compose.resources {
    packageOfResClass = "org.momosi.shutupandcharge.app.resources"
}

kotlin {
    android {
        namespace = "org.momosi.shutupandcharge.app"
    }

    iosArm64 {
        binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }
    iosSimulatorArm64 {
        binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.collections.immutable)
            implementation(projects.core.common)
            implementation(projects.core.navigation)
            implementation(projects.core.network)
            implementation(projects.core.uiLibrary)
            implementation(projects.core.user)
            implementation(projects.core.charger)
            implementation(projects.core.transaction)
            implementation(projects.core.account)
            implementation(projects.core.mock)

            implementation(projects.feature.welcome.api)
            implementation(projects.feature.welcome.impl)
            implementation(projects.feature.dashboard.api)
            implementation(projects.feature.dashboard.impl)
            implementation(projects.feature.transactions.api)
            implementation(projects.feature.transactions.impl)
            implementation(projects.feature.profile.api)
            implementation(projects.feature.profile.impl)
            implementation(projects.feature.admin.api)
            implementation(projects.feature.admin.impl)
        }
    }
}
