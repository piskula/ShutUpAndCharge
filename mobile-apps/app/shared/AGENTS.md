# app:shared

Wiring point of the app; produces the iOS `Shared.framework`.

* `SuacApp.kt` — root composable: theme, error snackbar host, bottom `NavigationBar` for main tabs,
  `NavDisplay` with every feature's `*Entries`.
* `SuacViewModel.kt` — exposes `access` (from `AuthManager`), `isAdmin` and `errors` (`ErrorReporter`).
* `AppAccess.kt` — `SignedOut` -> `WelcomeNavKey`, `Ready` -> `DashboardNavKey`; the only place that
  switches root destinations.
* `MainTabs.kt` — Dashboard, Transactions, Admin (admins only), Profile.
* `AppNavKeySerializers.kt` — register every `NavKey` that can be in the back stack.
* `di/AppModule.kt` — `allModules`, the only Koin aggregation. `initKoin` adds `:core:mock`'s
  `mockModule` on top when `AppConfig.useMockData` is on; `DemoModeBadge` then marks every screen.
* iosMain: `bootstrapIosApp(...)` and `MainViewController()` for Swift.