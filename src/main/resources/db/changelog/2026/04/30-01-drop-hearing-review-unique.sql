--liquibase formatted sql

--changeset polina:drop-hearing-review-unique-constraint splitStatements:false endDelimiter:;
-- Снимаем уникальность (submission_id, moderator_id), чтобы хранить историю рецензий/комментариев по слушанию.
ALTER TABLE hearing_review DROP CONSTRAINT IF EXISTS hearing_review_submission_id_moderator_id_key;

-- Если имя ограничения другое — снять единственное UNIQUE на таблице (в схеме после миграции 27-02 оно одно).
DO $$
DECLARE
  cname TEXT;
BEGIN
  SELECT c.conname INTO cname
  FROM pg_constraint c
  JOIN pg_class t ON c.conrelid = t.oid
  WHERE t.relname = 'hearing_review'
    AND c.contype = 'u'
  LIMIT 1;
  IF cname IS NOT NULL AND cname <> 'hearing_review_submission_id_moderator_id_key' THEN
    EXECUTE format('ALTER TABLE hearing_review DROP CONSTRAINT IF EXISTS %I', cname);
  END IF;
END $$;
