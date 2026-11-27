# app:ios

Xcode project (`iosApp.xcodeproj`), not a Gradle module. SwiftUI host that embeds the Compose UI.

* `iOSApp.swift` reads `SuacApiBaseUrl`, `SuacRealmUrl`, `SuacOAuthClientId`, `SuacAppScheme`,
  `SuacUseMockData` from `Info.plist` (filled from `Environment.xcconfig`) and calls `bootstrapIosApp(...)`.
* `Environment.xcconfig` has `prod` and `demo` environments; `APP_ENVIRONMENT = demo` runs on in-memory
  demo data (`:core:mock`, no Keycloak/backend) under the name "SUAC Demo". Never commit it set to `demo`.
* `ContentView.swift` hosts `MainViewController()` from `Shared.framework`.
* The framework is built by the "Compile Kotlin Framework" build phase:
  `./gradlew :app:shared:embedAndSignAppleFrameworkForXcode` from `mobile-apps/`.
* CocoaPods (`Podfile`) adds FirebaseCore and FirebaseCrashlytics. Open `iosApp.xcworkspace` after `pod install`.
* `GoogleService-Info.plist` is gitignored; CI fetches it. Without it crash reporting is disabled.
* OIDC uses `ASWebAuthenticationSession`, so no URL types are needed in `Info.plist`.