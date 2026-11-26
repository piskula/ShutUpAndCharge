---
name: create-feature-module
description: Use when creating a new product feature from scratch. Triggered by phrases like "create a new feature", "add a feature module", "scaffold a feature", "new feature module", "create feature/*/api", "create feature/*/impl", "add a new tab".
---

# Skill: Create a New Feature Module

> Scaffolds a complete `feature/<name>/api` + `feature/<name>/impl` module pair from scratch (paths relative to `mobile-apps/`), wired into the app. All modules are KMP (Android + `iosArm64` + `iosSimulatorArm64`) with `commonMain` source sets. Existing features to copy from: `welcome`, `dashboard`, `transactions`, `profile`, `admin`.

## Triggers

Load this skill when the task matches **any** of these:
- Adding a brand-new product feature that doesn't exist yet
- Creating a new `feature/*/api` or `feature/*/impl` module
- Task says "create a new feature", "add a feature module", or "scaffold a feature"

## Prerequisites

- `mobile-apps/settings.gradle.kts` is accessible — the new module paths must be added there (this is the mobile build's own settings file; the repo-root backend build is separate and must not be touched)
- `app/shared/build.gradle.kts`, `app/shared/src/commonMain/kotlin/org/momosi/shutupandcharge/app/di/AppModule.kt`, `SuacApp.kt` and `AppNavKeySerializers.kt` are accessible for wiring

## Steps

### 1. Create directory structure

```
feature/<name>/
  api/
    build.gradle.kts
    src/commonMain/kotlin/org/momosi/shutupandcharge/feature/<name>/api/
  impl/
    build.gradle.kts
    src/commonMain/kotlin/org/momosi/shutupandcharge/feature/<name>/impl/
    src/commonMain/composeResources/values/strings.xml
    src/commonMain/composeResources/values-sk/strings.xml
```

### 2. Create `feature/<name>/api/build.gradle.kts`

```kotlin
plugins {
    alias(libs.plugins.suac.feature.api)
}

kotlin {
    android {
        namespace = "org.momosi.shutupandcharge.feature.<name>.api"
    }
}
```

`suac.feature.api` already applies `suac.library` + kotlinx-serialization and exposes `androidx.navigation3:runtime`.

### 3. Create the entry-point nav key in `api/`

File: `feature/<name>/api/src/commonMain/kotlin/.../feature/<name>/api/<Name>NavKey.kt`

```kotlin
package org.momosi.shutupandcharge.feature.<name>.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object <Name>NavKey : NavKey
```

For nav keys with parameters, use `data class` instead of `data object`:
```kotlin
@Serializable
data class <Name>NavKey(val id: Long) : NavKey
```

### 4. Create `feature/<name>/impl/build.gradle.kts`

`suac.feature.impl` applies `suac.library`, `suac.koin`, `suac.compose` and kotlinx-serialization, and
already adds `:core:ui-library` and `:core:navigation`.

```kotlin
plugins {
    alias(libs.plugins.suac.feature.impl)
}

compose.resources {
    packageOfResClass = "org.momosi.shutupandcharge.feature.<name>.impl.resources"
    generateResClass = always
}

kotlin {
    android {
        namespace = "org.momosi.shutupandcharge.feature.<name>.impl"
    }
    sourceSets {
        commonMain.dependencies {
            api(projects.feature.<name>.api)
            implementation(projects.core.common)
            implementation(libs.kotlinx.collections.immutable)
            // Add as needed: projects.core.user, projects.core.charger, projects.core.transaction, projects.core.account
        }
    }
}
```

Add `alias(libs.plugins.suac.test)` to `plugins { }` if the module gets `commonTest` unit tests.

### 5. Create screen files — follow `add-screen-to-feature/SKILL.md` for each screen

For a single-screen feature, put the files directly in the `impl` root package, as the existing
features do: `feature/<name>/impl/src/commonMain/kotlin/.../feature/<name>/impl/`.
Required files: `<Name>Screen.kt`, `<Name>ViewModel.kt`, `<Name>State.kt`, `<Name>Action.kt`
Optional: `<Name>Event.kt` (only when one-shot VM→UI signals are needed)

Add every string to both `values/strings.xml` (English) and `values-sk/strings.xml` (Slovak) — see
`.claude/rules/string-resources.md`.

### 6. Create the entry provider in `impl/navigation/`

File: `feature/<name>/impl/src/commonMain/kotlin/.../feature/<name>/impl/navigation/<Name>EntryProvider.kt`

```kotlin
package org.momosi.shutupandcharge.feature.<name>.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import org.momosi.shutupandcharge.core.navigation.Navigator
import org.momosi.shutupandcharge.feature.<name>.api.<Name>NavKey
import org.momosi.shutupandcharge.feature.<name>.impl.<Name>Screen

fun EntryProviderScope<NavKey>.<name>Entries(navigator: Navigator) {
    entry<<Name>NavKey> {
        <Name>Screen(
            onBack = navigator::goBack,
        )
    }
}
```

Drop the `navigator` parameter if the screen never navigates (e.g. `transactionsEntries()`).

### 7. Create the Koin module in `impl/di/`

File: `feature/<name>/impl/src/commonMain/kotlin/.../feature/<name>/impl/di/<Name>Module.kt`

```kotlin
package org.momosi.shutupandcharge.feature.<name>.impl.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.momosi.shutupandcharge.feature.<name>.impl.<Name>ViewModel

val <name>Module = module {
    viewModel { <Name>ViewModel(get()) }
}
```

### 8. Register modules in `mobile-apps/settings.gradle.kts`

Add to the `include(...)` block (no extra prefix — module paths are relative to `mobile-apps/`):
```kotlin
":feature:<name>:api",
":feature:<name>:impl",
```

### 9. Wire into `app:shared`

In `app/shared/build.gradle.kts`, add to `commonMain.dependencies`:
```kotlin
implementation(projects.feature.<name>.api)
implementation(projects.feature.<name>.impl)
```

In `app/shared/src/commonMain/kotlin/.../app/di/AppModule.kt`, add `<name>Module` to the `allModules` list.

In `app/shared/src/commonMain/kotlin/.../app/SuacApp.kt`, call `<name>Entries(navigator)` inside the `entryProvider { }` block.

In `app/shared/src/commonMain/kotlin/.../app/AppNavKeySerializers.kt`, register the key:
`subclass(<Name>NavKey::class, <Name>NavKey.serializer())`.

If the feature is a bottom-navigation tab, add it to `MainTab` in `MainTabs.kt` (and filter it in
`mainTabsFor(...)` if it's admin-only, like `Admin`), add its `NavigationBarItem` mapping in
`SuacApp.kt`'s `navigationBarItems`, and add the tab label to `app/shared`'s compose resources in both
languages. Only `app:shared` decides root destinations (`AppAccess.kt`: `SignedOut` → Welcome,
`Ready` → Dashboard) — a feature never navigates between those itself.

### 10. Create module AGENTS.md files

Create `feature/<name>/api/AGENTS.md` and `feature/<name>/impl/AGENTS.md` (plus matching one-line
`CLAUDE.md` stubs containing `@AGENTS.md` — see `feature/profile/api` and `feature/profile/impl`).
Document the nav keys, screens, and dependencies.

## Verification

- [ ] `feature/<name>/api/` contains `build.gradle.kts` and one `*NavKey.kt` in `src/commonMain/kotlin/`
- [ ] `feature/<name>/impl/` contains `build.gradle.kts`, the screen files, an `EntryProvider`, a Koin module and `strings.xml` in both `values/` and `values-sk/`
- [ ] Both module paths appear in `mobile-apps/settings.gradle.kts`
- [ ] `app/shared/build.gradle.kts` references `projects.feature.<name>.api` and `projects.feature.<name>.impl`
- [ ] `AppModule.kt`'s `allModules` includes the new Koin module
- [ ] `SuacApp.kt` calls the new `<name>Entries(...)` and `AppNavKeySerializers.kt` registers `<Name>NavKey`
- [ ] `feature/<name>/impl` depends on no other feature's `impl` module
- [ ] `cd mobile-apps && ./gradlew :app:android:assembleDebug` passes
- [ ] `cd mobile-apps && ./gradlew spotlessCheck` passes
