# Shut Up And Charge — Mobile apps

Android and iOS clients for [Shut Up And Charge](https://charge.momosi.org/), built with Kotlin
Multiplatform and Compose Multiplatform. Architecture, modules and coding rules are described in
[`AGENTS.md`](AGENTS.md).

## Features

| Screen | Who | What |
|--------|-----|------|
| Welcome | everyone | Live charger status (SSE stream), sign in with Keycloak |
| Dashboard | users | Balance, live charger status with start/stop and charging current, last charging, monthly overview |
| Transactions | users | Paged list of chargings and top-ups, Excel export via the share sheet; admins also get filters (only mine, type) and the monthly kWh chart |
| Admin | admins | Users (top up credit, allow/block charging, assign RFID chip, copy Keycloak ID), pending transactions with enforced sync |
| Profile | users | Account info, language (English/Slovak), app/server version, sign out |

## Tech stack

Kotlin 2.4 · Compose Multiplatform 1.12 · Navigation 3 · Koin · Ktor 3 + Ktorfit · kotlinx.serialization ·
kotlin-multiplatform-oidc (Keycloak, PKCE) · Kermit · Firebase Crashlytics (GitLive SDK) ·
Spotless/ktlint · detekt. Android: AGP 9.5, minSdk 28, targetSdk 37. iOS: 15+, CocoaPods for Firebase.

## Environment

The app has a single (production) environment:

| Setting | Value |
|---------|-------|
| Application ID / bundle ID | `org.momosi.shutupandcharge` |
| API | `https://charge.momosi.org/` |
| Keycloak realm | `https://sso.momosi.org/realms/momosi/` |
| OAuth client | `mobile-client` |
| Redirect URIs | `org.momosi.shutupandcharge://auth/callback`, `org.momosi.shutupandcharge://auth/logout` |

Android reads these from `buildConfigField`s in `app/android/build.gradle.kts`, iOS from
`app/ios/Environment.xcconfig` (exposed through `Info.plist`).

### One-time Keycloak setup

Create a client in the `momosi` realm (the backend only validates the issuer, no backend change needed):

* Client ID `mobile-client`, **public** client (client authentication off), standard flow on, PKCE `S256`.
* Valid redirect URIs: `org.momosi.shutupandcharge://auth/callback`.
* Valid post logout redirect URIs: `org.momosi.shutupandcharge://auth/logout`.
* Realm roles (`MOMO_USER`, `MOMO_ADMIN`) must be included in the access token (`realm_access.roles`,
  the default `roles` client scope).
* The app requests only the `openid` scope (no `offline_access`), so its refresh token is tied to the
  Keycloak SSO session: users must sign in again after the realm's *SSO Session Idle* / *SSO Session Max*.

## Building locally

Requirements: JDK 21+ to run Gradle (the build uses a JDK 25 toolchain, auto-provisioned), Android SDK,
and for iOS macOS + Xcode + CocoaPods.

Firebase config files are **never committed**. Download them from the Firebase project
`shutupandcharge` (or with `npx firebase-tools apps:sdkconfig`):

* Android: `app/android/google-services.json` (required — the Google Services plugin fails without it).
* iOS: `app/ios/iosApp/GoogleService-Info.plist` (crash reporting is disabled when it is missing).

```bash
cd mobile-apps

./gradlew :app:android:assembleDebug        # debug APK
./gradlew :app:android:installDebug         # install on a connected device/emulator
./gradlew testAndroidHostTest               # unit tests
./gradlew spotlessApply                     # format
./gradlew detektCommonMainSourceSet detektAndroidMainSourceSet detektIosMainSourceSet :app:android:detekt
./gradlew :app:android:lintDebug
```

iOS:

```bash
cd mobile-apps/app/ios
pod install
open iosApp.xcworkspace   # build & run the iosApp scheme; the Kotlin framework is built by a build phase
```

### Demo mode (mock data)

Runs the whole app on in-memory fake data — no Keycloak, no backend — e.g. while the `suac-mobile`
Keycloak client does not exist yet. A yellow **Demo data** badge is shown on every screen.

```bash
cd mobile-apps
./gradlew :app:android:installMock          # or assembleMock -> app/android/build/outputs/apk/mock/
```

iOS: set `APP_ENVIRONMENT = demo` in `app/ios/Environment.xcconfig` (do not commit it).

* **Sign in** on Welcome signs in "Demo User" with user + admin roles, so every tab including Admin is
  reachable. Sign out returns to Welcome; restarting the app also signs out (nothing is persisted).
* Charger: refresh the charger (pull to refresh or the card's refresh button) to "plug in" the car, then
  **Start** — kWh grow every second (1 s = 1 charging minute at the chosen 6/10/16 A). **Stop** books the
  charging (0.30 €/kWh) to the demo balance and history; refresh again to unplug.
* 48 transactions (paged by 20), monthly summary and kWh chart, 8 accounts for Admin (top up, allow/block,
  RFID chip all change the in-memory state) and 3 pending transactions that **Sync** turns into chargings.
  Excel export shares a small CSV instead.
* The `mock` build type uses the production applicationId (Firebase only knows that one), so installing
  it replaces an installed debug/release build.

## CI/CD

Workflows live in the repository root `.github/` and only run for mobile changes
(`gradle.yml`, the server pipeline, ignores `mobile-apps/**`).

* **`mobile-build.yml`** — on PRs and pushes to `main` touching `mobile-apps/**`: Spotless, detekt, unit
  tests and Android Lint; debug APK; iOS build (signed ad-hoc `.ipa` when the iOS secrets exist, otherwise
  an unsigned Simulator app). On `main` it also refreshes the `mobile-latest` pre-release and distributes
  the debug builds to Firebase App Distribution (group `internal-testers`).
* **`mobile-release.yml`** — on tags `mobile-vMAJOR.MINOR.PATCH`: signed release APK + AAB
  (`versionCode = MAJOR*10000 + MINOR*100 + PATCH`), signed ad-hoc `.ipa`, GitHub Release, Firebase App
  Distribution, and the AAB to the Google Play internal track when `PLAY_SERVICE_ACCOUNT_JSON` is set.

Releasing: `git tag mobile-v1.0.0 && git push origin mobile-v1.0.0`.

### Secrets

| Secret | Used for |
|--------|----------|
| `FIREBASE_TOKEN` | firebase-tools (config download, App Distribution) |
| `SUAC_FIREBASE_ANDROID_APP_ID`, `SUAC_FIREBASE_IOS_APP_ID` | Firebase app IDs in project `shutupandcharge` |
| `ANDROID_KEYSTORE_BASE64`, `ANDROID_KEYSTORE_PASSWORD`, `ANDROID_KEY_ALIAS`, `ANDROID_KEY_PASSWORD` | Release signing |
| `IOS_TEAM_ID`, `IOS_DISTRIBUTION_CERTIFICATE_BASE64`, `IOS_DISTRIBUTION_CERTIFICATE_PASSWORD`, `IOS_ADHOC_PROFILE_BASE64` | iOS ad-hoc signing (optional; without them iOS builds are unsigned) |
| `PLAY_SERVICE_ACCOUNT_JSON` | Google Play upload (optional) |

Debug builds are signed with the committed `app/android/keystore/debug.keystore` so every machine and CI
produce the same debug signature.
