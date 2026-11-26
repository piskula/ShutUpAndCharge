# app:android

Thin Android shell (`com.android.application`). No screens, no business logic.

* `SuacApplication` builds `AppConfig` from `BuildConfig` and calls `initKoin` + `initApp` from `app:shared`.
* `SuacActivity` extends `AppCompatActivity` (required for `AppCompatDelegate.setApplicationLocales`
  language switching), registers `AndroidCodeAuthFlowFactory` and renders `SuacApp()`.
* Single environment (prod), no product flavors. `appScheme` is also the OIDC redirect scheme
  (`oidcRedirectScheme` manifest placeholder).
* Release signing comes from `-PandroidKeystoreFile/-PandroidKeystorePassword/-PandroidKeyAlias/-PandroidKeyPassword`
  (`suac.android.signing`); debug builds use the committed `keystore/debug.keystore`.
* `google-services.json` is gitignored; it must exist locally and is fetched in CI.