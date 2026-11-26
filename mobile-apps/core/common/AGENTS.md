# core:common

Base module: `Logger`, `CrashReporting`, `DispatcherProvider`, `ConnectivityManager`, `AppConfig`,
`AppEnvironment`, `LanguageRepository` (English/Slovak), `ErrorReporter`, `Page<T>`, `ApiException`
mapping, `runCatchingCancellable`, typed `combine` overloads, `DateFormatter` and `NumberFormatter`
(EUR and kWh formatting with the device locale).

* `VersionRepository` reads the public `GET /info/version` with its own bare client (`VersionApi`).
* `CrashReporting.init()` disables itself when Firebase is not configured (no `GoogleService-Info.plist`
  / `google-services.json`), so local builds without Firebase still run.
* Depends on `:core:dto` for `ErrorDto` and `BuildInfoDto`.