--liquibase formatted sql

--changeset polina:20260429-hearing-open-column
ALTER TABLE course_lesson ADD COLUMN IF NOT EXISTS hearing_open_for_students BOOLEAN DEFAULT TRUE;

UPDATE course_lesson SET hearing_open_for_students = TRUE WHERE category = 'HEARING' AND (hearing_stage IS NULL OR hearing_stage = 'TOPIC_APPROVAL');
UPDATE course_lesson SET hearing_open_for_students = FALSE WHERE category = 'HEARING' AND hearing_stage IS NOT NULL AND hearing_stage <> 'TOPIC_APPROVAL';

--changeset polina:20260429-add-conference-defense-lesson
INSERT INTO course_lesson (course_id, order_number, title, lecture_content, practice_description, category, hearing_stage, submission_type, max_score, submission_deadline, created_at, hearing_open_for_students)
SELECT c.id, 904, 'Защита на конференции', NULL, NULL, 'HEARING', 'CONFERENCE_DEFENSE', 'FILE', 0, NULL, NOW(), FALSE
FROM course c
WHERE EXISTS (
    SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.category = 'HEARING' AND cl.hearing_stage = 'FINAL'
)
  AND NOT EXISTS (
    SELECT 1 FROM course_lesson cl2 WHERE cl2.course_id = c.id AND cl2.hearing_stage = 'CONFERENCE_DEFENSE'
);