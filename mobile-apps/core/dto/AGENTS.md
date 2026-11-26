# core:dto

Hand-written `@Serializable` DTOs mirroring the backend contract (`../module-api/api-docs.json` and
`module-api/src/main/kotlin`). Pure Kotlin, only kotlinx.serialization.

* `BigDecimal` -> `Double`, date-times -> `String` (parse with `toInstantOrNull()` from `:core:network`).
* Give fields defaults where the backend may omit them; the shared `Json` ignores unknown keys.
* Keep DTO names equal to the backend ones with a `Dto` suffix. Mapping to domain models happens in
  the owning core module (`dto/*Mapper.kt`).