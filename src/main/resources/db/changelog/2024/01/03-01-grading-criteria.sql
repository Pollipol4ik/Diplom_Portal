-- liquibase formatted sql
-- changeset diplom:grading-criteria

-- ============================================================
-- Критерии оценивания, привязанные к курсу
-- ============================================================
CREATE TABLE IF NOT EXISTS grading_criterion (
    id              BIGSERIAL    PRIMARY KEY,
    course_id       BIGINT       NOT NULL,
    order_number    INT          NOT NULL,
    name            VARCHAR(500) NOT NULL,
    description     TEXT,
    max_points      INT          NOT NULL,

    CONSTRAINT fk_grading_criterion_course FOREIGN KEY (course_id)
        REFERENCES course(id) ON DELETE CASCADE,
    CONSTRAINT uq_grading_criterion_order UNIQUE (course_id, order_number)
);

CREATE INDEX idx_grading_criterion_course ON grading_criterion(course_id);

-- ============================================================
-- Оценка по каждому критерию за конкретную работу
-- ============================================================
CREATE TABLE IF NOT EXISTS submission_criterion_grade (
    id              BIGSERIAL   PRIMARY KEY,
    submission_id   BIGINT      NOT NULL,
    criterion_id    BIGINT      NOT NULL,
    points          INT         NOT NULL DEFAULT 0,

    CONSTRAINT fk_scg_submission FOREIGN KEY (submission_id)
        REFERENCES lesson_submission(id) ON DELETE CASCADE,
    CONSTRAINT fk_scg_criterion  FOREIGN KEY (criterion_id)
        REFERENCES grading_criterion(id) ON DELETE CASCADE,
    CONSTRAINT uq_scg_submission_criterion UNIQUE (submission_id, criterion_id)
);

CREATE INDEX idx_scg_submission ON submission_criterion_grade(submission_id);
CREATE INDEX idx_scg_criterion  ON submission_criterion_grade(criterion_id);
