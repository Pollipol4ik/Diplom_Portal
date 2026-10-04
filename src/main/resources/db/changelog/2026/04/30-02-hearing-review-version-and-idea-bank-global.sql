--liquibase formatted sql

--changeset polina:hearing-review-add-submission-version
ALTER TABLE hearing_review ADD COLUMN IF NOT EXISTS submission_version INT NOT NULL DEFAULT 1;

--changeset polina:idea-bank-make-global-and-score
-- Банк идей больше не привязан к школам. Добавляем score и снимаем внешние ключи к school/project.
ALTER TABLE idea_bank DROP CONSTRAINT IF EXISTS fk_ib_school;
ALTER TABLE idea_bank DROP CONSTRAINT IF EXISTS fk_ib_project;
DROP INDEX IF EXISTS idx_ib_school_id;
ALTER TABLE idea_bank DROP COLUMN IF EXISTS school_id;

ALTER TABLE idea_bank ADD COLUMN IF NOT EXISTS score INT NOT NULL DEFAULT 0;
CREATE INDEX IF NOT EXISTS idx_idea_bank_score ON idea_bank(score DESC);

