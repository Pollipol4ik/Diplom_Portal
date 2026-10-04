--liquibase formatted sql

--changeset polina:add-lesson-category-and-hearing-stage
ALTER TABLE course_lesson ADD COLUMN IF NOT EXISTS category VARCHAR(20) NOT NULL DEFAULT 'LESSON';
ALTER TABLE course_lesson ADD COLUMN IF NOT EXISTS hearing_stage VARCHAR(30);

--changeset polina:create-course-group
CREATE TABLE IF NOT EXISTS course_group (
    id          BIGSERIAL PRIMARY KEY,
    course_id   BIGINT       NOT NULL REFERENCES course(id) ON DELETE CASCADE,
    title       VARCHAR(200) NOT NULL,
    description TEXT,
    school_id   BIGINT       NOT NULL REFERENCES school(id),
    created_at  TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_course_group_course ON course_group(course_id);

--changeset polina:create-course-group-member
CREATE TABLE IF NOT EXISTS course_group_member (
    id         BIGSERIAL PRIMARY KEY,
    group_id   BIGINT    NOT NULL REFERENCES course_group(id) ON DELETE CASCADE,
    account_id BIGINT    NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    is_owner   BOOLEAN   NOT NULL DEFAULT FALSE,
    joined_at  TIMESTAMP NOT NULL DEFAULT now(),
    UNIQUE(group_id, account_id)
);

CREATE INDEX IF NOT EXISTS idx_course_group_member_group ON course_group_member(group_id);
CREATE INDEX IF NOT EXISTS idx_course_group_member_account ON course_group_member(account_id);

--changeset polina:create-hearing-submission
CREATE TABLE IF NOT EXISTS hearing_submission (
    id              BIGSERIAL   PRIMARY KEY,
    lesson_id       BIGINT      NOT NULL REFERENCES course_lesson(id) ON DELETE CASCADE,
    group_id        BIGINT      NOT NULL REFERENCES course_group(id) ON DELETE CASCADE,
    file_id         BIGINT      REFERENCES file(id),
    status          VARCHAR(30) NOT NULL DEFAULT 'ON_REVIEW',
    current_version INT         NOT NULL DEFAULT 1,
    submitted_at    TIMESTAMP   NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP   NOT NULL DEFAULT now(),
    UNIQUE(lesson_id, group_id)
);

CREATE INDEX IF NOT EXISTS idx_hearing_submission_lesson ON hearing_submission(lesson_id);
CREATE INDEX IF NOT EXISTS idx_hearing_submission_group ON hearing_submission(group_id);

--changeset polina:create-hearing-review
CREATE TABLE IF NOT EXISTS hearing_review (
    id            BIGSERIAL PRIMARY KEY,
    submission_id BIGINT    NOT NULL REFERENCES hearing_submission(id) ON DELETE CASCADE,
    moderator_id  BIGINT    NOT NULL REFERENCES account(id),
    comment       TEXT,
    grade         INT,
    reviewed_at   TIMESTAMP NOT NULL DEFAULT now(),
    UNIQUE(submission_id, moderator_id)
);

CREATE INDEX IF NOT EXISTS idx_hearing_review_submission ON hearing_review(submission_id);
