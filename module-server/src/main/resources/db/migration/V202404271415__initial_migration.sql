CREATE TABLE account
(
  id          INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  id_keycloak VARCHAR(255) NOT NULL UNIQUE,
  first_name  VARCHAR(255) NOT NULL,
  last_name   VARCHAR(255) NOT NULL,
  role        ENUM ('Admin', 'CompanyManager') DEFAULT NULL
);

CREATE TABLE transport_company
(
  id   INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL
);

CREATE TABLE transport_company_driver
(
  id         INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  company_id INT UNSIGNED NOT NULL,
  driver_id  INT UNSIGNED NOT NULL,
  CONSTRAINT fk_company_driver_to_company
    FOREIGN KEY (company_id) REFERENCES transport_company (id)
      ON DELETE CASCADE
      ON UPDATE RESTRICT,
  CONSTRAINT fk_company_driver_to_account
    FOREIGN KEY (driver_id) REFERENCES account (id)
      ON DELETE CASCADE
      ON UPDATE RESTRICT
);

CREATE TABLE transport_company_manager
(
  id         INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  company_id INT UNSIGNED NOT NULL,
  manager_id INT UNSIGNED NOT NULL,
  CONSTRAINT fk_company_manager_to_company
    FOREIGN KEY (company_id) REFERENCES transport_company (id)
      ON DELETE CASCADE
      ON UPDATE RESTRICT,
  CONSTRAINT fk_company_manager_to_account
    FOREIGN KEY (manager_id) REFERENCES account (id)
      ON DELETE CASCADE
      ON UPDATE RESTRICT
);

CREATE TABLE notification
(
  id          INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  company_id  INT UNSIGNED NOT NULL,
  valid_until DATE DEFAULT NULL
);
