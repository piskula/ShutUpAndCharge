---
name: add-repository
description: Use when adding a new repository, manager, or API data source to a core module. Triggered by phrases like "add a repository", "add a manager", "fetch from API", "expose data to features", "add data source", "new repository", "call a new endpoint", "load charger/transaction/account data".
---

# Skill: Add a Repository to a Core Module

> Adds a repository (or manager) to an existing `core/*` module (paths relative to `mobile-apps/`): domain model, DTO mapper, Ktorfit API client, repository interface + implementation, and Koin binding. All files go in `src/commonMain/kotlin/`. DTOs themselves live in `core:dto` (hand-written `@Serializable` classes mirroring the backend contract in `../module-api/api-docs.json`), not in the owning module — see Step 3.

## Triggers

Load this skill when the task matches **any** of these:
- Adding a new data source or backend resource to a `core/*` module (`core:charger`, `core:transaction`, `core:account`, …)
- Task says "add a repository", "add a manager", "fetch X from the API", "expose X to feature modules"
- A new backend API endpoint needs to be called from the app

## Prerequisites

- The target `core/<module>` already exists with a `build.gradle.kts` that applies `alias(libs.plugins.suac.ktor)` (KSP + Ktorfit + Ktor) and depends on `projects.core.common`, `projects.core.network` and `projects.core.dto` — add them if missing
- `core:network` is available and provides a Koin-managed `Ktorfit` instance backed by the authenticated `HttpClient` (base URL `https://charge.momosi.org/` and Keycloak bearer auth pre-configured), plus `toPage` (`core/network/.../dto/PageDtoMapper.kt`) for paginated responses. For Server-Sent Events there is a separate client qualified `named(SSE_HTTP_CLIENT)` — see `core:charger`'s `ChargerStatusStreamApi` for the pattern
- The endpoint exists in the backend contract `../module-api/api-docs.json` (hand-written OpenAPI 3.1, at the repo root). Mobile work must not change the backend build (`module-api`/`module-server`/`module-ui`); if the endpoint doesn't exist yet, stop and flag it as a separate backend change

## Steps

### 1. Create the domain model in `model/`

Place in `core/<module>/src/commonMain/kotlin/org/momosi/shutupandcharge/core/<module>/model/<Thing>.kt`.
Pure Kotlin only — no Android imports, no `Context`, no `@StringRes`.

```kotlin
package org.momosi.shutupandcharge.core.<module>.model

data class <Thing>(
    val id: Long,
    val name: String,
)
```

### 2. Create the repository interface

```kotlin
package org.momosi.shutupandcharge.core.<module>

import org.momosi.shutupandcharge.core.<module>.model.<Thing>
import org.momosi.shutupandcharge.core.common.model.Page

interface <Thing>Repository {

    suspend fun get<Things>(page: Int, size: Int): Result<Page<<Thing>>>

    suspend fun get<Thing>(id: Long): Result<<Thing>>
}
```

Rules: all methods `suspend` (or expose a `Flow` for live data, like `ChargerRepository.status`); never throw — return `Result<T>`, `T?`, or `List<T>`; no Android types.

### 3. Use (or add) the DTO in `core:dto`

DTOs live in `core/dto/src/commonMain/kotlin/org/momosi/shutupandcharge/dto/` (package `org.momosi.shutupandcharge.dto`, grouped per domain: `ChargerDtos.kt`, `TransactionDtos.kt`, `AccountDto.kt`, `CommonDtos.kt` with `PageDto`/`ErrorDto`/`BuildInfoDto`) — check there first for an existing `<Thing>Dto`. Only add a new one if it genuinely doesn't exist yet, and make its field names and types match the schema in `../module-api/api-docs.json` exactly:

```kotlin
// core/dto/src/commonMain/kotlin/org/momosi/shutupandcharge/dto/<Domain>Dtos.kt
package org.momosi.shutupandcharge.dto

import kotlinx.serialization.Serializable

@Serializable
data class <Thing>Dto(
    val id: Long,
    val name: String,
)
```

Conventions: no framework annotations besides `@Serializable`; give optional/nullable API fields a default (`= null`, `= emptyList()`) so a missing field doesn't fail decoding; keep date-times as `String` in the DTO and parse them in the mapper with `toInstantOrNull()` from `core:network` (`core/network/.../dto/InstantParsing.kt`), as `ChargerDtoMapper` does.

### 4. Create the DTO mapper in `dto/`

No local DTO class here — the mapper converts `core:dto`'s DTO straight to the domain model:

```kotlin
package org.momosi.shutupandcharge.core.<module>.dto

import org.momosi.shutupandcharge.core.<module>.model.<Thing>
import org.momosi.shutupandcharge.dto.<Thing>Dto

internal fun <Thing>Dto.to<Thing>(): <Thing> = <Thing>(
    id = id,
    name = name,
)
```

### 5. Create the Ktorfit API client in `api/`

Every backend API client is a Ktorfit interface in the owning core module — never a hand-written
class calling `HttpClient` directly (the only exception is an SSE stream, which Ktorfit can't model;
see `ChargerStatusStreamApi`). It returns `core:dto` types — no local response wrapper. Paths are
relative to the base URL and match `api-docs.json` (e.g. `api/...` for authenticated endpoints,
`info/...` for public ones).

```kotlin
package org.momosi.shutupandcharge.core.<module>.api

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import org.momosi.shutupandcharge.dto.<Thing>Dto

interface <Thing>Api {

    @GET("api/<thing>/{id}")
    suspend fun get<Thing>(@Path("id") id: Long): <Thing>Dto
}
```

For a paginated endpoint, return `core:dto`'s `PageDto<T>` and convert with `core:network`'s `toPage`:

```kotlin
@GET("api/<thing>")
suspend fun get<Thing>List(
    @Query("page") page: Int,
    @Query("size") size: Int,
    @Query("sort") sort: String?,
): PageDto<<Thing>Dto>
```
```kotlin
// in the repository impl
api.get<Thing>List(page, size, sort = null).toPage { it.to<Thing>() }
```

For a JSON body (the shared client already sets the JSON content type):
```kotlin
@POST("api/<thing>")
suspend fun create<Thing>(@Body body: <Thing>CreateDto): <Thing>Dto
```

Non-2xx responses surface as `ApiException` (`core:common/network`) through the shared client's
`installApiExceptionMapping` — map specific status codes (e.g. `ApiException.HttpError` with 409) in
the repository, not in the API interface.

### 6. Create the repository implementation

Wrap every call in `runCatchingCancellable { }` from `core:common` — never plain `runCatching`, which
swallows coroutine cancellation. Log failures with the `onNoConnectionFailure` / `onNetworkFailure`
helpers (`core:common/network`), as the existing repositories do.

```kotlin
package org.momosi.shutupandcharge.core.<module>

import org.momosi.shutupandcharge.core.<module>.api.<Thing>Api
import org.momosi.shutupandcharge.core.<module>.dto.to<Thing>
import org.momosi.shutupandcharge.core.<module>.model.<Thing>
import org.momosi.shutupandcharge.core.common.coroutines.runCatchingCancellable
import org.momosi.shutupandcharge.core.common.logger.Logger
import org.momosi.shutupandcharge.core.common.model.Page
import org.momosi.shutupandcharge.core.common.network.onNetworkFailure
import org.momosi.shutupandcharge.core.common.network.onNoConnectionFailure
import org.momosi.shutupandcharge.core.network.dto.toPage

private const val TAG = "<Thing>Repository"

internal class <Thing>RepositoryImpl(
    private val api: <Thing>Api,
) : <Thing>Repository {

    override suspend fun get<Things>(page: Int, size: Int): Result<Page<<Thing>>> = runCatchingCancellable {
        api.get<Thing>List(page, size, sort = null).toPage { it.to<Thing>() }
    }
        .onNoConnectionFailure { Logger.w(TAG, it, "Failed to load <things> (offline)") }
        .onNetworkFailure { Logger.e(TAG, it, "Failed to load <things>") }

    override suspend fun get<Thing>(id: Long): Result<<Thing>> = runCatchingCancellable {
        api.get<Thing>(id).to<Thing>()
    }
        .onNoConnectionFailure { Logger.w(TAG, it, "Failed to load <thing> (offline)") }
        .onNetworkFailure { Logger.e(TAG, it, "Failed to load <thing>") }
}
```

### 7. Add Koin bindings in `di/`

Add to the module's existing `di/<Module>Module.kt`, or create it if it doesn't exist.
`get<Ktorfit>()` is resolved from `networkModule` in `core:network`; `create<Thing>Api()` is the
extension KSP generates for the `<Thing>Api` interface.

```kotlin
package org.momosi.shutupandcharge.core.<module>.di

import de.jensklingenberg.ktorfit.Ktorfit
import org.koin.dsl.module
import org.momosi.shutupandcharge.core.<module>.<Thing>Repository
import org.momosi.shutupandcharge.core.<module>.<Thing>RepositoryImpl
import org.momosi.shutupandcharge.core.<module>.api.create<Thing>Api

val <module>Module = module {
    single { get<Ktorfit>().create<Thing>Api() }
    single<<Thing>Repository> { <Thing>RepositoryImpl(get()) }
}
```

### 8. Register in `AppModule` (new Koin module files only)

If this is a new `val xxxModule`, add it to the `allModules` list in `app/shared/src/commonMain/kotlin/org/momosi/shutupandcharge/app/di/AppModule.kt`
(and add `implementation(projects.core.<module>)` to `app/shared/build.gradle.kts` if the module is new).
If adding to an existing module file, no app-level change is needed.

### 9. Update `core/<module>/AGENTS.md`

If the module has an `AGENTS.md`, add the new repository interface and domain model to its Public API
section and the new files to its Key Files section. If it doesn't have one yet, create it (plus a
one-line `CLAUDE.md` containing `@AGENTS.md`, as in `core/network/`).

## Verification

- [ ] Domain model in `model/` contains no Android imports — placed in `src/commonMain/kotlin/`
- [ ] No local `<Thing>Dto` class in `core/<module>` — it's in `core/dto/src/commonMain/kotlin/org/momosi/shutupandcharge/dto/`, reused if it already existed, and matches `../module-api/api-docs.json`
- [ ] Repository interface returns only `Result<T>`, `T?`, `List<T>` or a `Flow` — never throws
- [ ] Repository implementation uses `runCatchingCancellable`, not `runCatching`
- [ ] DTO (in `core:dto`) uses `@Serializable`
- [ ] `<Thing>Api` is a Ktorfit interface (no `HttpClient` calls) and the module applies `suac.ktor`
- [ ] Koin module has `single { get<Ktorfit>().create<Thing>Api() }` and `single<<Thing>Repository> { ... }`
- [ ] No files outside `mobile-apps/` changed
- [ ] `cd mobile-apps && ./gradlew :app:android:assembleDebug` passes (it compiles every module the app depends on)
- [ ] If the module applies `suac.test` and you added mapper tests in `commonTest`: `cd mobile-apps && ./gradlew :core:<module>:testAndroidHostTest` passes
- [ ] `cd mobile-apps && ./gradlew spotlessCheck` passes
