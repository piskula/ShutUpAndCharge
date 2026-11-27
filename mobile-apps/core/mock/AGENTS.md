# core:mock

In-memory demo backend for **demo mode** (`AppConfig.useMockData`), so the whole app can be clicked
through without Keycloak or `https://charge.momosi.org/`. Never used by production builds.

* `mockModule` (`di/MockModule.kt`) overrides `AuthManager`, `UserRepository`, `ChargerRepository`,
  `TransactionRepository`, `AccountRepository` and `VersionRepository`. `app:shared`'s `initKoin` loads it
  after `allModules` (with `allowOverride(true)`) only when `useMockData` is on; real modules stay bound
  but their network/OIDC objects are never created.
* `MockBackend` is the single in-memory "server" (accounts, finished + pending transactions) behind a
  `Mutex`; every mock repository goes through it so actions show up on every screen. `MockData` seeds
  it deterministically (8 accounts, 48 transactions over ~4 months, 3 pending).
* `MockAuthManager` — "Sign in" immediately signs in `DemoUser` (roles User + Admin, account id 1);
  the session is memory-only, so an app restart returns to Welcome.
* `MockChargerRepository` — `Free` -(refresh: car plugs in)-> `CarConnected` -(start)-> `Charging`
  (kWh grow every second, one real second = one charging minute at the selected 6/10/16 A) -(stop)->
  `Complete` (records a charging for the demo account and charges 0.30 €/kWh) -(refresh)-> `Free`.
* Every call waits `MOCK_LATENCY` (350 ms) so loading states are visible; failures are `ApiException`s
  wrapped in `Result`, like the real repositories.
* Export produces a small CSV shared through the real platform `FileSharer`.
