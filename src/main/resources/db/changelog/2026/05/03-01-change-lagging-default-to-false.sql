--liquibase formatted sql

-- ============================================================
-- Исправление: is_lagging по умолчанию false (не отстающий).
-- Старое значение true приводило к тому, что новые ученики
-- сразу помечались отстающими до запуска крон-проверки.
-- ============================================================

--changeset fix:change-lagging-default-to-false
ALTER TABLE account ALTER COLUMN is_lagging SET DEFAULT false;

-- Сбрасываем флаг у тех, кто уже состоит в группе с загруженной работой по TOPIC_APPROVAL
UPDATE account SET is_lagging = false
WHERE id IN (
    SELECT DISTINCT cgm.account_id
    FROM course_group_member cgm
             JOIN course_group cg ON cg.id = cgm.group_id
             JOIN hearing_submission hs ON hs.group_id = cg.id
             JOIN course_lesson cl ON cl.id = hs.lesson_id
    WHERE cl.hearing_stage = 'TOPIC_APPROVAL'
);
