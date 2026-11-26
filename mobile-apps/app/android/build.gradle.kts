import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.momosi.shutupandcharge.utils.stringProperty

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.suac.spotless)
    alias(libs.plugins.suac.android.signing)
    alias(libs.plugins.suac.detekt)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

private val appScheme = "org.momosi.shutupandcharge"

android {
    namespace = "org.momosi.shutupandcharge"
    compileSdk = 37

    defaultConfig {
        applicationId = "org.momosi.shutupandcharge"
        targetSdk = 37
        minSdk = 28
        proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

        versionCode = stringProperty("appVersionCode")?.toIntOrNull() ?: 1
        versionName = stringProperty("appVersionName") ?: "dev"

        buildConfigField("String", "API_BASE_URL", "\"https://charge.momosi.org/\"")
        buildConfigField("String", "REALM_URL", "\"https://sso.momosi.org/realms/momosi/\"")
        buildConfigField("String", "OAUTH_CLIENT_ID", "\"mobile-client\"")
        buildConfigField("String", "APP_SCHEME", "\"$appScheme\"")
        buildConfigField("boolean", "USE_MOCK_DATA", "false")
        manifestPlaceholders["oidcRedirectScheme"] = appScheme
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
        }
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
        }
        // Demo build on in-memory data (no Keycloak, no backend). Same applicationId as prod because
        // google-services.json only knows that one, so it replaces an installed debug/release app.
        create("mock") {
            initWith(getByName("debug"))
            matchingFallbacks += "debug"
            buildConfigField("boolean", "USE_MOCK_DATA", "true")
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    lint {
        toolchain.languageVersion.set(JavaLanguageVersion.of(25))
        sarifReport = true
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll(
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=kotlinx.coroutines.FlowPreview",
            "-opt-in=kotlinx.serialization.ExperimentalSerializationApi",
        )
    }
}

dependencies {
    implementation(projects.app.shared)
    implementation(projects.core.common)
    implementation(projects.core.user)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
}
