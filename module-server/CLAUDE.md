# module-server

Spring Boot 4 backend (Kotlin, Java 21). Organized into domain packages under `sk.momosilabs.suac.server`:
- `account/` — endpoints for managing user accounts
- `dashboard/` — homepage data after login, account balance, last transactions
- `transaction/` — finished and temporary transaction endpoints and their scheduled pairing process
- `security/` — Security Config and public (non-auth) current user endpoint
- `info/` — public (non-auth) endpoints for version and charger status
- `common/` — shared config and utilities

## Layering Rules

```
Controller → UseCase → Persistence (or shared Service)
```

- **Controllers** implement the `module-api` interface and inject use cases only. No persistence calls, no business logic.
- **Use cases** contain business logic. They call persistence or shared services (e.g. `CurrentUserService`, `ExcelExportService`). **Use cases never call other use cases.** If two use cases need the same query, both call persistence independently.
- **Persistence** handles all data access.

Each use case lives in its own package under `.service.<useCaseName>/` with two files:
- `<UseCaseName>UseCase.kt` — interface
- `<UseCaseName>.kt` — `@Service open class` implementing the interface, with `@IsUser`/`@IsAdmin` and `@Transactional` on the override method

## Entity & Persistence Patterns

**Entity class:**
- Regular Kotlin class (NOT a data class). Fields as constructor properties.
- `@Entity(name = "table_name")` with explicit lowercase snake_case name. No `@Table` annotation.
- ID: `val id: Long = 0L` with `@Id @GeneratedValue(strategy = GenerationType.IDENTITY)`. Default `0L` allows construction without a DB round-trip.
- For manually assigned PKs (e.g. config tables), omit `@GeneratedValue`.
- Non-nullable fields: Kotlin non-nullable type + `@field:NotNull` (note the `@field:` target prefix).
- Nullable fields: Kotlin `?` suffix only.
- `val` for immutable fields (id, FK refs); `var` for fields that get updated after creation.
- **Relationships: always `@ManyToOne` only — never `@OneToMany`, never bidirectional.** This is a deliberate rule.
- No shared base entity, no `@MappedSuperclass`, no `@Embeddable`.
- `LocalDateTime` for timestamps; field names suffixed with `Utc` (e.g. `startedAtUtc`).

**Package layout per domain:**
```
<domain>/
  entity/
    <Name>Entity.kt
  persistence/
    <Name>Persistence.kt              ← interface (business-level operations on model/DTO objects)
    <Name>PersistenceProvider.kt      ← @Repository open class implementing the interface
    repository/
      <Name>Repository.kt             ← JpaRepository<Entity, Id> interface
    mapper/
      <Name>EntityMapper.kt           ← extension fns: fun Entity.toModel() = Model(...)
```

**Persistence provider:**
- `@Repository open class` implementing the `<Name>Persistence` interface.
- Constructor-injects repositories (and `EntityManager` if QueryDSL is needed).
- Read methods: `@Transactional(readOnly = true)`. Write methods: `@Transactional`.
- Throws `GlobalNotFoundException("fieldName=value not found")` for missing records.

**Mapper:**
- File-level extension functions only — no mapper class.
- `fun EntityClass.toModel() = ModelClass(...)` for entity → model.
- `fun ModelToCreate.asNewEntity(resolver: (id) -> RelatedEntity) = Entity(...)` for model → entity when FK resolution is needed.

## File Download Pattern

All file responses use `GenericFile` (in `common/model/`) with factory methods (`asExcelFile`, `asPdfFile`, `asPngFile`, `asZipFile`). Controllers return `ResponseEntity<ByteArrayResource>` via the `GenericFile.mapToResponseEntity()` extension in `common/controller/mapper/FileMapper.kt`. Use cases return `GenericFile`, not `ByteArrayResource` directly.

## ExcelExportService

`ExcelExportService.exportEntityToStream()` is the shared service for all Excel exports.

## QueryDSL Notes

**`when` is a reserved keyword in Kotlin** — use backtick escaping with `CaseBuilder`:
```kotlin
CaseBuilder().`when`(transaction.price.lt(BigDecimal.ZERO)).then(1L).otherwise(0L).sum()
```

**`fetchResults()` does not work with GROUP BY** — for grouped queries, run two separate queries: one `fetch()` for content and one for the count (`.select(keys).groupBy(keys).fetch().size.toLong()`).

**Define aggregation expressions at class level** (not inside functions) so they can be referenced in ORDER BY without re-instantiation.

## Security Config Note

`anyRequest().permitAll()` in Spring Security is intentional — tightening it breaks Angular SPA routes (e.g. `/authenticated/home`) which are served as static files from the JAR. Actual endpoint security is enforced via `@IsUser`/`@IsAdmin` on use case methods. This will be revisited when the frontend is served from NGINX instead of the JAR.
