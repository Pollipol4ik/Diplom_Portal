-- ============================================================
-- Миграция: Course <-> School — ManyToMany
-- ============================================================

CREATE TABLE IF NOT EXISTS course_school (
    course_id  BIGINT NOT NULL,
    school_id  BIGINT NOT NULL,
    PRIMARY KEY (course_id, school_id),
    CONSTRAINT fk_course_school_course FOREIGN KEY (course_id)
        REFERENCES course(id) ON DELETE CASCADE,
    CONSTRAINT fk_course_school_school FOREIGN KEY (school_id)
        REFERENCES school(id) ON DELETE CASCADE
);

CREATE INDEX idx_course_school_course_id ON course_school(course_id);
CREATE INDEX idx_course_school_school_id ON course_school(school_id);

-- Перенос существующих данных из course.school_id в join-таблицу
INSERT INTO course_school (course_id, school_id)
SELECT id, school_id FROM course WHERE school_id IS NOT NULL
ON CONFLICT DO NOTHING;

-- Удаляем FK и колонку school_id из course
ALTER TABLE course DROP CONSTRAINT IF EXISTS fk_course_school;
DROP INDEX IF EXISTS idx_course_school_id;
ALTER TABLE course DROP COLUMN IF EXISTS school_id;

-- Добавляем колонку score в lesson_submission если её нет
-- (уже существует, но на всякий случай)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'lesson_submission' AND column_name = 'score'
    ) THEN
        ALTER TABLE lesson_submission ADD COLUMN score INTEGER;
    END IF;
END $$;
