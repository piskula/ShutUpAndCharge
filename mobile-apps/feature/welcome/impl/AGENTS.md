# feature:welcome:impl

Signed-out landing screen: illustration, live public charger status (`ChargerStatusCard` with
`isSignedIn = false`) and the Sign in button (`AuthManager.signIn()`). It never navigates; a successful
sign-in changes `AppAccess` and `app:shared` moves to the dashboard.