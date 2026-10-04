--changeset Polina_Kuptsova:update-subject-topic-to-string
-- Удаляем старую таблицу, так как логика полностью меняется
DROP TABLE IF EXISTS subject_topic CASCADE;
DROP TABLE IF EXISTS topic_type CASCADE;

-- Создаем новую таблицу с текстовым названием топика
create sequence if not exists subject_topic_pk_seq start 1 increment 1;
CREATE TABLE subject_topic (
                               id bigint primary key default nextval('subject_topic_pk_seq'),
                               name TEXT NOT NULL,
                               subject_id BIGINT NOT NULL REFERENCES subject(id) ON DELETE CASCADE,
                               CONSTRAINT subject_topic_name_unq UNIQUE (subject_id, name)
);

