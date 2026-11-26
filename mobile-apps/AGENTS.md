# Shut Up And Charge Mobile — Agent Instructions

Kotlin Multiplatform client (Android + iOS) for the Shut Up And Charge EV charger backend. It is a
**standalone Gradle build** (own `settings.gradle.kts`, `gradlew`, `gradle/libs.versions.toml` and
`build-logic/`) that lives next to the backend build in the same repository. The root `./gradlew build`
never touches it, and mobile work must not change the backend (`module-api`, `module-server`,
`module-ui`). Run every mobile Gradle command from `mobile-apps/`.

The architecture mirrors the TruckTrack client (`piskula/TruckerTracker`), adapted to this domain.

* **UI** — Compose Multiplatform only. No XML layouts, no SwiftUI screens.
* **Dependency Injection** — Koin. No annotation processing.
* **Networking** — Ktor client + Ktorfit interfaces, kotlinx.serialization.
* **Libraries** — Use entries from `gradle/libs.versions.toml`. Before adding one, confirm it
  publishes Kotlin/Native artifacts for `iosArm64` and `iosSimulatorArm64`.
* **Concurrency** — Kotlin coroutines and Flows only.

See `README.md` for build/run instructions, Keycloak/Firebase setup, CI and releasing.

## Project Structure

Root package: `org.momosi.shutupandcharge`. Gradle paths below are relative to `mobile-apps/`.

* **`app/android`** (`:app:android`) and **`app/ios`** — thin platform shells. `app/ios` is an Xcode
  project, not a Gradle module; it consumes `:app:shared`'s static `Shared.framework` through
  `./gradlew :app:shared:embedAndSignAppleFrameworkForXcode` (run by an Xcode build phase).
* **`app/shared`** (`:app:shared`) — root composable `SuacApp`, root `SuacViewModel`, the bottom
  navigation tabs, Koin aggregation (`di/AppModule.kt`) and platform bootstrap.
* **`feature/*`** — one product feature each, split into `api` (navigation key only) and `impl`
  (ViewModel, Composables, State/Action). Today: `welcome`, `dashboard`, `transactions`, `profile`, `admin`.
* **`core/*`** — shared domain logic and infrastructure: `common`, `dto`, `network`, `user`, `charger`,
  `transaction`, `account`, `navigation`, `ui-library`, and `mock` (in-memory demo backend, see
  [Demo mode](#demo-mode)).

Every module has its own `AGENTS.md` describing its responsibilities — read it before changing that
module and record module-specific rules there.

### Backend contract

The backend contract is the hand-written OpenAPI spec `../module-api/api-docs.json` plus the Kotlin DTOs
in `../module-api/src/main/kotlin`. The mobile app keeps hand-written `@Serializable` copies of the DTOs
it needs in `:core:dto` (the counterpart of TruckTrack's shared DTO build). When the backend contract
changes, update `:core:dto` and the affected Ktorfit interfaces in the same change.

Useful backend facts:

* Auth: Keycloak realm `momosi`; the backend validates JWTs by issuer only, so any client of the realm
  works. Roles are realm roles `MOMO_USER` (needed for every `/api/**` call) and `MOMO_ADMIN`.
* `BigDecimal` values arrive as JSON numbers (`Double` in DTOs); `OffsetDateTime` values as ISO strings,
  parsed with `toInstantOrNull()` from `:core:network`.
* Errors are `ErrorDTO { userMessage, id }`; 4xx `userMessage`s are shown to the user.
* For non-admins the backend restricts transaction lists, exports and charts to the caller.

### Module Dependency Rules

* `feature/*/api` — depends only on Navigation 3 runtime (via `suac.feature.api`). Contains only a `NavKey`.
* `feature/*/impl` — depends on its own `api`, `core` modules, and other features' `api` modules only to
  reference their `NavKey`. **Never depend on another feature's `impl`.** Shared logic goes into `core`.
* `core` modules — may depend on other `core` modules (acyclic). **Never depend on features.**
  `:core:ui-library` exposes `:core:charger` and `:core:transaction` because `ChargerStatusCard` and
  `TransactionRow` are shared by several features.
* `app:shared` — depends on all `feature/*/impl` and `core` modules.
* `app:android` — depends on `app:shared`, `core:common`, `core:user`. Thin Android shell only.

---

## Coding Guidelines

### Architecture — MVVM

* **Composable** — presenter. Receives immutable `state`, delegates interactions via `onAction`
  callbacks or `on<Action>` lambdas. Never holds business state.
* **ViewModel** — extends `ViewModel()` directly. Exposes a single `StateFlow<State>` built with
  `combine(...).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), initial)`. Talks to the
  `core` layer only. Collect state with `collectAsStateWithLifecycle()`.
* Prefer several focused `MutableStateFlow`s (one writer each) combined into the exposed state over one
  big flow mutated with `.copy()`. `core:common`'s `Flows.kt` has typed `combine` overloads up to 8 flows.
* **State** — `@Immutable` data classes / sealed interfaces, no lambdas, no `Throwable`s (map failures
  with `toFailureReason()`). Lists are `ImmutableList`.
* **Action** — `sealed interface` for UI-to-VM intents. One package per screen:
  `XxxScreen.kt`, `XxxViewModel.kt`, `XxxState.kt`, `XxxAction.kt`.

### Data Layer

* **Repository** — data retrieval and source abstraction; **Manager** — orchestration
  (e.g. `TransactionExportManager`: export + share).
* Public interface + implementation in the same module, bound with `single<Interface> { Impl(get()) }`.
* **Never throw across public interfaces.** Return `Result<T>`. Inside suspend code use
  `runCatchingCancellable { }` from `core:common`, never `runCatching { }`.
* Failures reaching the UI are `ApiException` (`NoConnection`, `Unauthorized`, `HttpError`). Report
  transient action failures with `ErrorReporter.report(it)`; the root shows them in a snackbar.
  Full-screen load failures render `ErrorState` from `:core:ui-library`.

### Navigation

* Navigation 3. Keys are `@Serializable data object`s implementing `NavKey` in `feature/*/api` and
  registered in `app:shared`'s `AppNavKeySerializers.kt`.
* Entries are registered via `EntryProviderScope<NavKey>` extensions in `feature/*/impl/navigation/`.
* Root destinations are decided only by `app:shared` from `AppAccess` (`SignedOut` → welcome,
  `Ready` → dashboard). Welcome never navigates itself; signing in changes the access.
* Main tabs (`MainTabs.kt`): Dashboard, Transactions, Admin (only for `MOMO_ADMIN`), Profile. Tabs are
  opened with `navigator.navigate(root = DashboardNavKey, destination = tab)`.

### Koin

* ViewModels: `viewModel { XxxViewModel(get(), …) }`, injected with `koinViewModel()`.
* One Koin module per core/feature module, aggregated only in `app:shared`'s `allModules`.
  `:core:mock`'s `mockModule` is the exception: `initKoin` loads it on top of `allModules` only in demo mode.
* Platform bindings use `expect fun platformXxxModule(): Module` (see `core:transaction`'s `FileSharer`).

### Demo mode

`AppConfig.useMockData` (Android `mock` build type, iOS `APP_ENVIRONMENT = demo`) replaces Keycloak and
the backend with `:core:mock`'s in-memory fakes and shows a "Demo data" badge. When you add a method to a
repository interface that `:core:mock` overrides, implement it in the matching `Mock*Repository` too.

### UI Library

* All reusable components live in `:core:ui-library`. Feature modules must not import
  `androidx.compose.material3` — use `foundation`, `ui` and `:core:ui-library` components.
* Theme via `SuacTheme { }` / `AppTheme.colors` / `AppTheme.typography` (SUAC Azure `#005CBB`).
* Icons only through `SuacIcons`; illustrations only through `SuacIllustrations`.
* Every composable file has `@Preview`s of its stateless content (see `.claude/rules/compose-previews.md`).

### Networking

* `:core:network` provides the authenticated `HttpClient`, `Ktorfit`, and an SSE client
  (`named(SSE_HTTP_CLIENT)`) used for the live charger status stream.
* **Every backend API client is a Ktorfit interface** in the owning core module, created in its Koin
  module with `get<Ktorfit>().createXxxApi()`. Exceptions: unauthenticated bootstrap clients
  (`core:common`'s `VersionApi`, `core:user`'s `AuthApi`) and the SSE stream (`core:charger`'s
  `ChargerStatusStreamApi`) which Ktorfit cannot express.
* URLs, OAuth client ID and redirect scheme come from Koin-injected `AppConfig` — never hardcode them.
* Retries are off by default; opt in per request only for idempotent calls.

### Strings

* Compose resources in every module that has UI. Every user-facing string exists in **English**
  (`values/strings.xml`) and **Slovak** (`values-sk/strings.xml`) — see `.claude/rules/string-resources.md`.
* Never build user-facing strings in ViewModels except number/date formatting via
  `NumberFormatter`/`DateFormatter` from `core:common`.

### Logging & Crash Reporting

* Use `Logger` from `core:common` with a tag; it forwards to Crashlytics when Firebase is configured.
* `CrashReporting` silently disables itself when no Firebase config is bundled (local iOS builds).

### Style

* `./gradlew spotlessApply` before committing (a pre-commit hook in `.githooks/` does it for staged
  files). No wildcard imports. Prefer `private`/`internal`.
* Only comment code that needs clarification.

## KMP Rules

1. New source goes in `src/commonMain/kotlin/` unless it calls platform APIs.
2. Platform behaviour: interface/`expect` in `commonMain`, implementations in **both** `androidMain` and
   `iosMain`.
3. No `android.*` types in `commonMain`; use `String` for URIs and pass data, not `Context`.

## Verification

```bash
./gradlew spotlessCheck
./gradlew detektCommonMainSourceSet detektAndroidMainSourceSet detektIosMainSourceSet :app:android:detekt
./gradlew testAndroidHostTest
./gradlew :app:android:lintDebug :app:android:assembleDebug
```

iOS compilation needs macOS; CI builds the iOS app on every PR.

## Agent Skills

Agent skills live in `.claude/skills/` (scaffolding features/screens, adding repositories, Spotless,
CI failures, releasing, iOS signing, testing with adb). Prefer the skill over improvising the steps.
