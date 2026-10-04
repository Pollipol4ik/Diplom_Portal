-- ============================================================
-- Курсы проектной деятельности
-- ============================================================

CREATE TABLE IF NOT EXISTS course (
    id              BIGSERIAL    PRIMARY KEY,
    name            VARCHAR(300) NOT NULL,
    description     TEXT,
    school_id       BIGINT       NOT NULL,
    is_active       BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_course_school FOREIGN KEY (school_id)
        REFERENCES school(id) ON DELETE CASCADE
);

CREATE INDEX idx_course_school_id ON course(school_id);
CREATE INDEX idx_course_is_active ON course(is_active);

-- ============================================================
-- Уроки (темы) внутри курса
-- ============================================================

CREATE TABLE IF NOT EXISTS course_lesson (
    id                    BIGSERIAL    PRIMARY KEY,
    course_id             BIGINT       NOT NULL,
    order_number          INT          NOT NULL,
    title                 VARCHAR(500) NOT NULL,
    lecture_content        TEXT,
    practice_description   TEXT,
    submission_type       VARCHAR(30)  NOT NULL DEFAULT 'TEXT',
    created_at            TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_course_lesson_course FOREIGN KEY (course_id)
        REFERENCES course(id) ON DELETE CASCADE,
    CONSTRAINT uq_course_lesson_order UNIQUE (course_id, order_number),
    CONSTRAINT chk_submission_type CHECK (submission_type IN ('TEXT', 'FILE', 'TEXT_AND_FILE'))
);

CREATE INDEX idx_course_lesson_course_id ON course_lesson(course_id);

-- ============================================================
-- Работы учеников по практическим заданиям
-- ============================================================

CREATE TABLE IF NOT EXISTS lesson_submission (
    id                BIGSERIAL    PRIMARY KEY,
    lesson_id         BIGINT       NOT NULL,
    account_id        BIGINT       NOT NULL,
    text_content      TEXT,
    file_id           BIGINT,
    status            VARCHAR(30)  NOT NULL DEFAULT 'SUBMITTED',
    reviewer_comment  TEXT,
    submitted_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_lesson_submission_lesson  FOREIGN KEY (lesson_id)
        REFERENCES course_lesson(id) ON DELETE CASCADE,
    CONSTRAINT fk_lesson_submission_account FOREIGN KEY (account_id)
        REFERENCES account(id) ON DELETE CASCADE,
    CONSTRAINT fk_lesson_submission_file    FOREIGN KEY (file_id)
        REFERENCES file(id) ON DELETE SET NULL,
    CONSTRAINT uq_lesson_submission_student UNIQUE (lesson_id, account_id),
    CONSTRAINT chk_submission_status CHECK (status IN ('SUBMITTED', 'ACCEPTED', 'NEEDS_REVISION'))
);

CREATE INDEX idx_lesson_submission_lesson_id  ON lesson_submission(lesson_id);
CREATE INDEX idx_lesson_submission_account_id ON lesson_submission(account_id);
CREATE INDEX idx_lesson_submission_status     ON lesson_submission(status);
