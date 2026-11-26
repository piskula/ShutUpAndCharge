# core:network

Ktor/Ktorfit infrastructure for all data modules.

* `networkModule` provides the shared `Json`, the authenticated `HttpClient` (bearer token from
  `AuthManager`, `Accept-Language`, logging, `ApiException` mapping, opt-in retries), `Ktorfit`, and an
  SSE client `named(SSE_HTTP_CLIENT)` (SSE plugin, 30 s socket timeout, header-level logging only).
* A final 401 invalidates the local session for the rejected token; 403/5xx/offline do not.
* `dto/PageDtoMapper.kt` maps `PageDto<T>` to `Page<T>`; `dto/InstantParsing.kt` parses backend
  date-times (`OffsetDateTime`/`ZonedDateTime`/no zone) to `Instant`.
* Tests in `src/commonTest` (`:core:network:testAndroidHostTest`).