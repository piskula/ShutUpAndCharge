pluginManagement {
    includeBuild("build-logic")

    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
    }
}

plugins {
    id("suac.git-hooks")
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        google()
        mavenCentral()
    }
}
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "suac-mobile"
include(
    ":app:android",
    ":app:shared",
    ":core:common",
    ":core:dto",
    ":core:ui-library",
    ":core:navigation",
    ":core:network",
    ":core:user",
    ":core:charger",
    ":core:transaction",
    ":core:account",
    ":feature:welcome:api",
    ":feature:welcome:impl",
    ":feature:dashboard:api",
    ":feature:dashboard:impl",
    ":feature:transactions:api",
    ":feature:transactions:impl",
    ":feature:profile:api",
    ":feature:profile:impl",
    ":feature:admin:api",
    ":feature:admin:impl",
)
