ALTER TABLE account
  ADD COLUMN verified_for_charging BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE charging_finished
  ADD COLUMN price DECIMAL(7, 2) NOT NULL DEFAULT 0;
