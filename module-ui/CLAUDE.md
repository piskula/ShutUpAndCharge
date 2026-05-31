# module-ui

Angular 21 SPA. Uses Keycloak JS 26.2.4 for browser-side OIDC login. API calls use the generated TypeScript client from `build/generated-sources/module-api/`. Angular Material for UI components. Organized into `authenticated/`, `non-authenticated/`, and `common/` directories.

## Building

Always build the frontend via Gradle — Node.js and Yarn are managed by the Gradle Node Plugin and live in `module-ui/.gradle`, not on the system PATH. Running `npm` directly may use a different Node version.

## File Download Pattern

`exportFn` passed to `PaginatedTableComponent` must return `Observable<HttpResponse<Blob>>` (with the `'response'` observe option so headers are accessible). `PaginatedTableComponent.onExport()` owns the download logic and reads the filename from the `Content-Disposition` header via `downloadFile()` in `common/download.util.ts`. The `exportFn` itself should be a pure data function with no download side effects.
