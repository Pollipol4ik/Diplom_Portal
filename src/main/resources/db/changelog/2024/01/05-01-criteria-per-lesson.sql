-- ============================================================
-- Миграция: критерии оценивания привязаны к уроку, а не к курсу
-- ============================================================

ALTER TABLE grading_criterion ADD COLUMN IF NOT EXISTS lesson_id BIGINT;

-- Перенос: назначаем существующие критерии первому уроку курса (если есть)
UPDATE grading_criterion gc
SET lesson_id = (
    SELECT cl.id FROM course_lesson cl
    WHERE cl.course_id = gc.course_id
    ORDER BY cl.order_number ASC
    LIMIT 1
)
WHERE gc.lesson_id IS NULL;

-- Удалить критерии без урока (если у курса нет уроков)
DELETE FROM grading_criterion WHERE lesson_id IS NULL;

-- Убрать старый constraint и индекс
ALTER TABLE grading_criterion DROP CONSTRAINT IF EXISTS uq_grading_criterion_order;
DROP INDEX IF EXISTS idx_grading_criterion_course;

-- Убрать NOT NULL с course_id, потом удалить
ALTER TABLE grading_criterion ALTER COLUMN course_id DROP NOT NULL;

-- Добавить FK на lesson
ALTER TABLE grading_criterion ADD CONSTRAINT fk_grading_criterion_lesson
    FOREIGN KEY (lesson_id) REFERENCES course_lesson(id) ON DELETE CASCADE;

-- Новый unique constraint
ALTER TABLE grading_criterion ADD CONSTRAINT uq_grading_criterion_lesson_order
    UNIQUE (lesson_id, order_number);

CREATE INDEX idx_grading_criterion_lesson ON grading_criterion(lesson_id);

-- Убрать старую колонку course_id
ALTER TABLE grading_criterion DROP COLUMN IF EXISTS course_id;
