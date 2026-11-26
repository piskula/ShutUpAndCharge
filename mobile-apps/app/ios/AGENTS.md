# app:ios

Xcode project (`iosApp.xcodeproj`), not a Gradle module. SwiftUI host that embeds the Compose UI.

* `iOSApp.swift` reads `SuacApiBaseUrl`, `SuacRealmUrl`, `SuacOAuthClientId`, `SuacAppScheme` from
  `Info.plist` (filled from `Environment.xcconfig`) and calls `bootstrapIosApp(...)`.
* `ContentView.swift` hosts `MainViewController()` from `Shared.framework`.
* The framework is built by the "Compile Kotlin Framework" build phase:
  `./gradlew :app:shared:embedAndSignAppleFrameworkForXcode` from `mobile-apps/`.
* CocoaPods (`Podfile`) adds FirebaseCore and FirebaseCrashlytics. Open `iosApp.xcworkspace` after `pod install`.
* `GoogleService-Info.plist` is gitignored; CI fetches it. Without it crash reporting is disabled.
* OIDC uses `ASWebAuthenticationSession`, so no URL types are needed in `Info.plist`.