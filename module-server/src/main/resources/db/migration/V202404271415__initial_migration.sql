CREATE TABLE account
(
  id          BIGSERIAL PRIMARY KEY,
  id_keycloak VARCHAR(255) NOT NULL UNIQUE,
  first_name  VARCHAR(255) NOT NULL,
  last_name   VARCHAR(255) NOT NULL
);

CREATE TABLE charging_finished
(
  id         BIGSERIAL PRIMARY KEY,
  guid       UUID NOT NULL,
  account_id BIGINT NOT NULL REFERENCES account (id)
      ON DELETE CASCADE ON UPDATE RESTRICT,
  time       TIMESTAMP NOT NULL,
  kWh        DECIMAL(7, 3),
  station_id VARCHAR(255) NOT NULL
);
