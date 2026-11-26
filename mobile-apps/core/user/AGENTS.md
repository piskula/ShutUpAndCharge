# core:user

Keycloak OIDC authentication via kotlin-multiplatform-oidc (authorization code + PKCE, scopes
`openid offline_access`), JWT verification against the realm public key, token storage and refresh.

* `AuthManager` — `authenticationState`, `signIn()`, `signOut()` (needs a connection), `token()`,
  `invalidateSession()`.
* `UserRepository.user` — `User(id = JWT sub = Keycloak ID, name, email, username, roles)`.
* Roles come from `realm_access.roles`: `MOMO_USER` -> `UserRole.User`, `MOMO_ADMIN` -> `UserRole.Admin`.
  A user without `MOMO_USER` can sign in but every `/api/**` call returns 403.
* Android stores tokens in SharedPreferences, iOS in NSUserDefaults. Android must register the
  activity with `AndroidCodeAuthFlowFactory.registerActivity` (done in `SuacActivity`).