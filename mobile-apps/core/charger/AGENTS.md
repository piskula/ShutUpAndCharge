# core:charger

Charger status and control.

* `ChargerRepository.status` — hot `Flow<ChargerStatus>` shared while subscribed: the SSE stream
  `GET info/chargerStatus/stream` (event `status`), reconnecting every 5 s and falling back to
  `GET info/chargerStatus` between attempts. Results of `refresh()`, `startCharging()` and
  `stopCharging()` are merged in.
* `ChargerApi` (Ktorfit): `info/chargerStatus`, `api/dashboard/start`, `api/dashboard/stop`,
  `api/dashboard/setChargingParams?current=`.
* `ChargerStatusStreamApi` is a hand-written Ktor SSE client because Ktorfit cannot express SSE.
* `ChargerState` maps backend `CarStateDTO`: `UnknownOrError` -> `Offline`, `Idle` -> `Free`,
  `WaitCar` -> `CarConnected`, `Charging`, `Complete`, `Error`. Start is enabled only for `CarConnected`.