--liquibase formatted sql

--changeset polina:drop-idea-bank-project-fk
ALTER TABLE idea_bank DROP CONSTRAINT IF EXISTS fk_ib_project;

--changeset polina:drop-old-project-tables
DROP TABLE IF EXISTS submission_file_history CASCADE;
DROP TABLE IF EXISTS stage_review CASCADE;
DROP TABLE IF EXISTS project_stage_submission CASCADE;
DROP TABLE IF EXISTS project_member CASCADE;
DROP TABLE IF EXISTS project CASCADE;

DROP SEQUENCE IF EXISTS submission_file_history_pk_seq;
DROP SEQUENCE IF EXISTS stage_review_pk_seq;
DROP SEQUENCE IF EXISTS project_stage_submission_pk_seq;
DROP SEQUENCE IF EXISTS project_member_pk_seq;
DROP SEQUENCE IF EXISTS project_pk_seq;

DROP INDEX IF EXISTS idx_submission_history_submission;
DROP INDEX IF EXISTS idx_review_submission;
DROP INDEX IF EXISTS idx_submission_project;
DROP INDEX IF EXISTS idx_project_member_account;
DROP INDEX IF EXISTS idx_project_member_project;
DROP INDEX IF EXISTS idx_project_school;
