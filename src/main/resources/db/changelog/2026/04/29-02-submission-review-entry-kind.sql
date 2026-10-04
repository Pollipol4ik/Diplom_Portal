-- Тип записи в истории проверки: проверка модератора или ответ ученика
ALTER TABLE submission_review_history
    ADD COLUMN IF NOT EXISTS entry_kind VARCHAR(30) NOT NULL DEFAULT 'MODERATOR';

COMMENT ON COLUMN submission_review_history.entry_kind IS 'MODERATOR | STUDENT_REPLY';
