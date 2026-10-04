--liquibase formatted sql

-- ============================================================
-- Добавление истории версий файлов для подач этапов.
-- Каждый раз при замене файла (до проверки или при доработке)
-- старая версия сохраняется в эту таблицу.
-- Отзывы модераторов (stage_review) остаются неизменными.
-- ============================================================

--changeset Polina Kuptsova:20-add-current-version-to-submission
alter table project_stage_submission
    add column if not exists current_version int not null default 1;

--changeset Polina Kuptsova:21-create-submission-file-history-table
create sequence if not exists submission_file_history_pk_seq start 1 increment 1;

create table if not exists submission_file_history (
                                                       id              bigint    primary key default nextval('submission_file_history_pk_seq'),
                                                       submission_id   bigint    not null references project_stage_submission(id) on delete cascade,
                                                       file_id         bigint    references file(id) on delete set null,
                                                       version_number  int       not null,
                                                       uploaded_at     timestamp not null default now(),
                                                       status_at_upload text check (status_at_upload in ('ON_REVIEW', 'NEEDS_REVISION', 'ACCEPTED', 'REJECTED')),
                                                       constraint submission_history_version_unq unique (submission_id, version_number)
);

create index if not exists idx_submission_history_submission
    on submission_file_history(submission_id);
