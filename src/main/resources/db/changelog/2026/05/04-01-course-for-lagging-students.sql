-- Курс для отстающих: автоматическое создание группы при дедлайне / при привязке школы

--changeset Diplom_Backend:2026-05-04-01-course-for-lagging-students
ALTER TABLE course ADD COLUMN IF NOT EXISTS for_lagging_students BOOLEAN NOT NULL DEFAULT FALSE;
