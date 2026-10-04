--liquibase formatted sql

--changeset Diplom_Backend:2026-05-01-01-lagging-default-true
-- Изначально все ученики считаются отстающими, пока тема не выбрана и не согласована в курсе.

ALTER TABLE account
    ALTER COLUMN is_lagging SET DEFAULT TRUE;

-- Перевести существующих учеников в статус "отстающий" по умолчанию.
UPDATE account a
SET is_lagging = TRUE
WHERE a.role_id = (SELECT r.id FROM role r WHERE r.name = 'ROLE_USER');

