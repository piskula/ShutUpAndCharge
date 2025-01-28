CREATE TABLE charging_ongoing
(
  id             BIGSERIAL PRIMARY KEY,
  trx_number     SERIAL      NOT NULL,
  trx_identifier VARCHAR(63) NOT NULL,
  energy_meter   BIGSERIAL   NOT NULL,
  time_start_utc TIMESTAMP   NOT NULL,
  account_id     BIGINT      NOT NULL REFERENCES account (id)
    ON DELETE CASCADE ON UPDATE RESTRICT
);
