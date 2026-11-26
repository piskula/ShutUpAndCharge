# core:transaction

Transactions and charging history.

* `TransactionRepository` — finished transactions (`POST api/transaction/finished`, sort `timeStartUtc,desc`),
  Excel export (`POST api/transaction/finished/export`, file name from `Content-Disposition`), monthly kWh
  chart, last chargings and monthly summary (`api/dashboard/*`), pending transactions and enforced sync
  (`api/transaction/temporary*`, admin only).
* `TransactionFilter` -> `TransactionFilterDto`: `onlyAccountKeycloakId` -> `accountIds`, type -> `onlyCredit`.
  The backend ignores the account filter for non-admins and always scopes to the caller.
* A transaction with `price > 0` is a top-up (credit), otherwise a charging.
* `display/TransactionDisplay` — formatted row data shared by dashboard and transactions.
* `export/TransactionExportManager` downloads the export and hands it to the platform `FileSharer`
  (Android: cache file + `FileProvider` + share chooser; iOS: temp file + `UIActivityViewController`).