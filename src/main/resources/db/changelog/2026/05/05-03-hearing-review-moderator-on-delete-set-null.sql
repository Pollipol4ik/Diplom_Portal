--liquibase formatted sql

--changeset Diplom_Backend:2026-05-05-03-hearing-review-moderator-on-delete-set-null
ALTER TABLE hearing_review DROP CONSTRAINT IF EXISTS hearing_review_moderator_id_fkey;

ALTER TABLE hearing_review ALTER COLUMN moderator_id DROP NOT NULL;

ALTER TABLE hearing_review
    ADD CONSTRAINT hearing_review_moderator_id_fkey
        FOREIGN KEY (moderator_id) REFERENCES account(id) ON DELETE SET NULL;