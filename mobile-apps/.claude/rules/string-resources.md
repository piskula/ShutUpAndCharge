---
paths:
  - "**/composeResources/**"
  - "app/android/src/main/res/**/strings.xml"
---

# String Resources & Copywriting

String resources use Compose Multiplatform resources (`compose.resources`). Each module that needs
strings declares `compose.resources { packageOfResClass = "..." }` (e.g.
`org.momosi.shutupandcharge.feature.transactions.impl.resources`) and keeps them in
`src/commonMain/composeResources/`.

Every new or changed user-facing string must be added in **both** supported languages in the same
change: English in the module's default `values/strings.xml` and Slovak in the matching
`values-sk/strings.xml`. A string added to only one of the two is an incomplete change.

Compose resources are not Android resources: write apostrophes and quotes as-is (`Couldn't`), never
escape them (`Couldn\'t`) — Compose shows the backslash literally. Escaping is only needed in Android
`res/values/strings.xml` files (`app/android/src/main/res/`).

When writing string resources, act as a technical copywriter — precise about domain concepts, but
writing for a non-developer user of a shared EV charger.

* Write for a non-technical user — someone who charges their electric car at the shared charger, or
  an admin who manages its users and balances, and understands charging but not software.
* One or two short sentences maximum.
* Plain language. No jargon unless immediately explained.
* Active voice, present tense.
* Name the specific thing. Not "some data" or "certain features" — say which (the charger, the
  charging session, your balance, the RFID chip).
* Be technically accurate about EV charging concepts — kWh for energy, A (amps) for charging
  current, a charging session vs. a top-up of the balance.
