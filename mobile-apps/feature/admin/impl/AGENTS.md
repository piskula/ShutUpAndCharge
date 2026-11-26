# feature:admin:impl

Administration for `MOMO_ADMIN` users, mirroring the web user management:

* Users tab: paged accounts with balance, charging permission and RFID indicator. A bottom sheet offers
  top up credit (with 10/20/50/100 EUR presets), allow/block charging (confirmation), assign/remove RFID chip
  and copy the Keycloak ID.
* Pending tab: temporary (not yet processed) transactions and "Enforce sync".
* Tests: `:feature:admin:impl:testAndroidHostTest`.