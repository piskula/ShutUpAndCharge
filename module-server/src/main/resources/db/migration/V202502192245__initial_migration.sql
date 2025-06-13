CREATE TABLE account
(
  id                    BIGSERIAL PRIMARY KEY,
  id_keycloak           VARCHAR(255) NOT NULL UNIQUE,
  first_name            VARCHAR(255) NOT NULL,
  last_name             VARCHAR(255) NOT NULL,
  verified_for_charging BOOLEAN      NOT NULL DEFAULT FALSE,
  assigned_chip_uid     VARCHAR(255)          DEFAULT NULL
);

CREATE TABLE charging_finished
(
  id                    BIGSERIAL PRIMARY KEY,
  guid                  UUID          NOT NULL,
  account_id            BIGINT        NOT NULL REFERENCES account (id) ON DELETE CASCADE ON UPDATE RESTRICT,
  time_start_utc        TIMESTAMP     NOT NULL,
  kwh                   DECIMAL(7, 3) NOT NULL,
  station_id            VARCHAR(255) DEFAULT NULL,
  price                 DECIMAL(7, 2) NOT NULL,
  station_session       BIGINT       DEFAULT NULL,
  energy_meter          BIGINT       DEFAULT NULL,
  triggered_by_chip_uid VARCHAR(255) DEFAULT NULL,
  link                  VARCHAR(511) DEFAULT NULL,
  UNIQUE (station_id, energy_meter)
);

CREATE TABLE charging_ongoing
(
  id             BIGSERIAL PRIMARY KEY,
  trx_number     SERIAL      NOT NULL,
  trx_identifier VARCHAR(63) NOT NULL,
  energy_meter   BIGINT      NOT NULL,
  time_start_utc TIMESTAMP   NOT NULL,
  account_id     BIGINT      NOT NULL REFERENCES account (id) ON DELETE CASCADE ON UPDATE RESTRICT
);

CREATE TABLE station_config
(
  station_id                          VARCHAR(255) PRIMARY KEY,
  price_per_kwh                       DECIMAL(6, 3) NOT NULL,
  last_success_download_timestamp_utc TIMESTAMP NOT NULL
);

INSERT INTO station_config(station_id, price_per_kwh, last_success_download_timestamp_utc)
VALUES ('ETCC:Kutlik:1', 0.25, TIMESTAMP '2025-05-14 00:00:00');
