import Shared
import SwiftUI

@main
struct iOSApp: App {
    init() {
        #if DEBUG
        let isDebug = true
        #else
        let isDebug = false
        #endif
        IosAppInitializerKt.bootstrapIosApp(
            isDebug: isDebug,
            apiBaseUrl: Self.infoValue("SuacApiBaseUrl"),
            realmUrl: Self.infoValue("SuacRealmUrl"),
            oauthClientId: Self.infoValue("SuacOAuthClientId"),
            appScheme: Self.infoValue("SuacAppScheme")
        )
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }

    private static func infoValue(_ key: String) -> String {
        guard let value = Bundle.main.object(forInfoDictionaryKey: key) as? String, !value.isEmpty else {
            fatalError("Missing \(key) in Info.plist")
        }
        return value
    }
}