--liquibase formatted sql

--changeset Polina Kuptsova:2026-05-05-01-idea-bank-course-id
ALTER TABLE idea_bank ADD COLUMN IF NOT EXISTS course_id BIGINT REFERENCES course(id) ON DELETE SET NULL;