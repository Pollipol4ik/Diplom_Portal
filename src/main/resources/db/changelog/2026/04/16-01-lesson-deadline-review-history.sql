-- Дедлайн сдачи работы по уроку
ALTER TABLE course_lesson ADD COLUMN IF NOT EXISTS submission_deadline TIMESTAMP;

-- История проверок (комментарии и оценки)
CREATE TABLE IF NOT EXISTS submission_review_history (
    id                    BIGSERIAL    PRIMARY KEY,
    submission_id         BIGINT       NOT NULL,
    reviewer_account_id   BIGINT,
    status_after          VARCHAR(30)  NOT NULL,
    score                 INT,
    reviewer_comment      TEXT,
    criteria_snapshot_json TEXT,
    created_at            TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_srh_submission FOREIGN KEY (submission_id)
        REFERENCES lesson_submission(id) ON DELETE CASCADE,
    CONSTRAINT fk_srh_reviewer FOREIGN KEY (reviewer_account_id)
        REFERENCES account(id) ON DELETE SET NULL,
    CONSTRAINT chk_srh_status CHECK (status_after IN ('SUBMITTED', 'ACCEPTED', 'NEEDS_REVISION'))
);

CREATE INDEX IF NOT EXISTS idx_srh_submission_id ON submission_review_history(submission_id);
