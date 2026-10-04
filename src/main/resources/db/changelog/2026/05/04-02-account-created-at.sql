--liquibase formatted sql

ALTER TABLE account ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT now();

UPDATE account SET created_at = now() WHERE created_at IS NULL;

ALTER TABLE account ALTER COLUMN created_at SET NOT NULL;

ALTER TABLE account ALTER COLUMN created_at SET DEFAULT now();
