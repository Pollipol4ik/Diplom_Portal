-- liquibase formatted sql
-- changeset diplom:course-moderators-and-grading

-- ============================================================
-- Модераторы курсов
-- ============================================================
CREATE TABLE IF NOT EXISTS course_moderator (
    id          BIGSERIAL   PRIMARY KEY,
    account_id  BIGINT      NOT NULL,
    course_id   BIGINT      NOT NULL,
    assigned_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_course_moderator_account FOREIGN KEY (account_id)
        REFERENCES account(id) ON DELETE CASCADE,
    CONSTRAINT fk_course_moderator_course  FOREIGN KEY (course_id)
        REFERENCES course(id) ON DELETE CASCADE,
    CONSTRAINT uq_course_moderator UNIQUE (account_id, course_id)
);

CREATE INDEX idx_course_moderator_account ON course_moderator(account_id);
CREATE INDEX idx_course_moderator_course  ON course_moderator(course_id);

-- ============================================================
-- Оценка за практическую работу (0-100 баллов)
-- ============================================================
ALTER TABLE lesson_submission ADD COLUMN IF NOT EXISTS score INTEGER;

-- ============================================================
-- Файл лекционного материала (прикреплённый к уроку)
-- ============================================================
ALTER TABLE course_lesson ADD COLUMN IF NOT EXISTS lecture_file_id BIGINT;
ALTER TABLE course_lesson ADD CONSTRAINT fk_course_lesson_lecture_file
    FOREIGN KEY (lecture_file_id) REFERENCES file(id) ON DELETE SET NULL;

-- ============================================================
-- Видео-ссылка для лекции
-- ============================================================
ALTER TABLE course_lesson ADD COLUMN IF NOT EXISTS video_url VARCHAR(1000);

-- ============================================================
-- Максимальный балл за урок (по умолчанию 100)
-- ============================================================
ALTER TABLE course_lesson ADD COLUMN IF NOT EXISTS max_score INTEGER NOT NULL DEFAULT 100;
