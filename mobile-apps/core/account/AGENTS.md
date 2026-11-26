# core:account

Account data: my balance (`GET api/dashboard/balance`) and admin account management
(`GET api/account`, `POST api/account/{id}/topUp` with a JSON number body,
`POST api/account/{id}/verified/{verified}`, `POST api/account/{id}/chipUid` with the raw chip UID as body,
empty string clears it).