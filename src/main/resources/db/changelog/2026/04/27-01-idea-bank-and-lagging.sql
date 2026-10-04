-- Банк идей — архив тем проектов для повторного использования
CREATE TABLE IF NOT EXISTS idea_bank (
    id                BIGSERIAL    PRIMARY KEY,
    title             VARCHAR(200) NOT NULL,
    description       TEXT,
    comments          TEXT,
    school_id         BIGINT       NOT NULL,
    created_by_id     BIGINT       NOT NULL,
    source_project_id BIGINT,
    created_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_ib_school     FOREIGN KEY (school_id)     REFERENCES school(id)   ON DELETE CASCADE,
    CONSTRAINT fk_ib_created_by FOREIGN KEY (created_by_id) REFERENCES account(id)  ON DELETE CASCADE,
    CONSTRAINT fk_ib_project    FOREIGN KEY (source_project_id) REFERENCES project(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_ib_school_id ON idea_bank(school_id);

-- Флаг отстающего ученика
ALTER TABLE account ADD COLUMN IF NOT EXISTS is_lagging BOOLEAN NOT NULL DEFAULT FALSE;

-- Дедлайн выбора темы проекта (per-school)
ALTER TABLE school ADD COLUMN IF NOT EXISTS topic_deadline DATE;
