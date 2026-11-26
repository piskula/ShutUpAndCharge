# app:android

Thin Android shell (`com.android.application`). No screens, no business logic.

* `SuacApplication` builds `AppConfig` from `BuildConfig` and calls `initKoin` + `initApp` from `app:shared`.
* `SuacActivity` extends `AppCompatActivity` (required for `AppCompatDelegate.setApplicationLocales`
  language switching), registers `AndroidCodeAuthFlowFactory` and renders `SuacApp()`.
* Single environment (prod), no product flavors. `appScheme` is also the OIDC redirect scheme
  (`oidcRedirectScheme` manifest placeholder).
* `mock` build type (`initWith(debug)`, same applicationId) sets `BuildConfig.USE_MOCK_DATA` -> demo mode
  on in-memory data (`:core:mock`); `src/mock/res` renames the launcher label to "SUAC Demo".
* Release signing comes from `-PandroidKeystoreFile/-PandroidKeystorePassword/-PandroidKeyAlias/-PandroidKeyPassword`
  (`suac.android.signing`); debug builds use the committed `keystore/debug.keystore`.
* `google-services.json` is gitignored; it must exist locally and is fetched in CI.