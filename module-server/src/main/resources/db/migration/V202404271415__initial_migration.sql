CREATE TYPE ACCOUNT_ROLE AS ENUM ('Admin', 'CompanyManager');

CREATE TABLE account
(
  id          BIGSERIAL PRIMARY KEY,
  id_keycloak VARCHAR(255) NOT NULL UNIQUE,
  first_name  VARCHAR(255) NOT NULL,
  last_name   VARCHAR(255) NOT NULL,
  role        ACCOUNT_ROLE DEFAULT NULL
);

CREATE TABLE transport_company
(
  id   BIGSERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL
);

CREATE TABLE transport_company_driver
(
  id         BIGSERIAL PRIMARY KEY,
  company_id BIGINT REFERENCES transport_company (id)
    ON DELETE CASCADE ON UPDATE RESTRICT,
  driver_id  BIGINT REFERENCES account (id)
    ON DELETE CASCADE ON UPDATE RESTRICT
);

CREATE TABLE transport_company_manager
(
  id         BIGSERIAL PRIMARY KEY,
  company_id BIGINT REFERENCES transport_company (id)
    ON DELETE CASCADE ON UPDATE RESTRICT,
  admin_id   BIGINT REFERENCES account (id)
    ON DELETE CASCADE ON UPDATE RESTRICT
);

CREATE TABLE notification
(
  id          BIGSERIAL PRIMARY KEY,
  company_id  BIGINT NOT NULL,
  valid_until DATE DEFAULT NULL
);
