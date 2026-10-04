--liquibase formatted sql

--changeset Diplom_Backend:2026-05-05-02-course-is-introduction
ALTER TABLE course ADD COLUMN IF NOT EXISTS is_introduction BOOLEAN NOT NULL DEFAULT FALSE;

COMMENT ON COLUMN course.is_introduction IS 'Вводный курс: виден всем ученикам школы (и обычным, и отстающим). Используется для ознакомления до распределения на целевые курсы.';