---
paths:
  - "app/**/*.kt"
  - "core/**/*.kt"
  - "feature/**/*.kt"
---

# Compose Previews

Keep `@Preview` functions in the same Kotlin file as the composable they preview.
Shared composables and their previews live together in `commonMain`; `suac.compose`
already provides the multiplatform preview annotation there. Platform-specific composables
keep their previews beside them in the matching platform source set.

* Every file with `@Composable` functions has `@Preview` functions for its key composables.
* Wrap the previewed content in `SuacTheme { }`.
* Preview functions are `private` and named with a `Preview` suffix — e.g. `ChargerStatusCardChargingPreview`.
* Populate sample data with values a real shared EV charger would produce — a plausible user name,
  a realistic charged amount (e.g. `12.4 kWh`), a believable balance or top-up in euros, a real
  date. Don't preview blank or `null` fields unless that empty variant is the thing being previewed.
* Never preview a top-level screen composable that requires a `ViewModel`. Preview the inner
  stateless content composable instead.
