--liquibase formatted sql

-- ============================================================
-- Назначение модераторов на школы (администратор назначает)
-- Отдельно от subject_moderator: здесь модератор курирует
-- все проекты школы, а не конкретный предмет
-- ============================================================

--changeset Polina Kuptsova:15-create-school-moderator-table
create sequence if not exists school_moderator_pk_seq start 1 increment 1;
create table if not exists school_moderator (
                                                id          bigint    primary key default nextval('school_moderator_pk_seq'),
                                                account_id  bigint    not null references account(id) on delete cascade,
                                                school_id   bigint    not null references school(id)  on delete cascade,
                                                assigned_at timestamp not null default now(),
                                                constraint school_moderator_unq unique (account_id, school_id)
);

-- ============================================================
-- Проект (основная сущность)
-- school_id фиксирует, какой школе принадлежит проект —
-- это требуется для фильтрации модераторов без избыточности
-- ============================================================

--changeset Polina Kuptsova:16-create-project-table
create sequence if not exists project_pk_seq start 1 increment 1;
create table if not exists project (
                                       id          bigint    primary key default nextval('project_pk_seq'),
                                       title       text      not null check (length(title) > 0),
                                       description text,
                                       school_id   bigint    not null references school(id),
                                       created_at  timestamp not null default now()
);

-- ============================================================
-- Участники проекта — нормализованная связь M:N
-- (project x account), флаг is_owner указывает автора.
-- Вынесено отдельно, чтобы избежать массивов в project.
-- ============================================================

--changeset Polina Kuptsova:17-create-project-member-table
create sequence if not exists project_member_pk_seq start 1 increment 1;
create table if not exists project_member (
                                              id         bigint    primary key default nextval('project_member_pk_seq'),
                                              project_id bigint    not null references project(id) on delete cascade,
                                              account_id bigint    not null references account(id) on delete cascade,
                                              is_owner   bool      not null default false,
                                              joined_at  timestamp not null default now(),
                                              constraint project_member_unq unique (project_id, account_id)
);

-- ============================================================
-- Подача на этап (1 запись = 1 этап конкретного проекта).
-- stage_number: 1, 2 или 3.
-- status: строковое представление enum ProjectStatus.
-- Уникальность по (project_id, stage_number) — нельзя дважды
-- подать один и тот же этап.
-- ============================================================

--changeset Polina Kuptsova:18-create-project-stage-submission-table
create sequence if not exists project_stage_submission_pk_seq start 1 increment 1;
create table if not exists project_stage_submission (
                                                        id           bigint    primary key default nextval('project_stage_submission_pk_seq'),
                                                        project_id   bigint    not null references project(id) on delete cascade,
                                                        stage_number text NOT NULL CHECK (stage_number IN ('STAGE_1','STAGE_2','STAGE_3')),
                                                        status       text      not null default 'ON_REVIEW'
                                                            check (status in ('ON_REVIEW','NEEDS_REVISION','ACCEPTED','REJECTED')),
                                                        file_id      bigint    references file(id) on delete set null,
                                                        submitted_at timestamp not null default now(),
                                                        updated_at   timestamp not null default now(),
                                                        constraint stage_submission_unq unique (project_id, stage_number)
);

-- ============================================================
-- Отзыв модератора на конкретную подачу этапа.
-- Один модератор — один отзыв на одну подачу (unique).
-- grade: опциональная оценка 1–10.
-- Вынесено отдельно от submission, чтобы соблюсти 3НФ
-- (оценка зависит от модератора + submission, а не только
-- от submission).
-- ============================================================

--changeset Polina Kuptsova:19-create-stage-review-table
create sequence if not exists stage_review_pk_seq start 1 increment 1;
create table if not exists stage_review (
                                            id            bigint    primary key default nextval('stage_review_pk_seq'),
                                            submission_id bigint    not null references project_stage_submission(id) on delete cascade,
                                            moderator_id  bigint    not null references account(id),
                                            comment       text,
                                            grade         int       check (grade between 1 and 10),
                                            reviewed_at   timestamp not null default now(),
                                            constraint stage_review_unq unique (submission_id, moderator_id)
);

-- Индексы для частых запросов
create index if not exists idx_project_school          on project(school_id);
create index if not exists idx_project_member_project  on project_member(project_id);
create index if not exists idx_project_member_account  on project_member(account_id);
create index if not exists idx_submission_project      on project_stage_submission(project_id);
create index if not exists idx_review_submission       on stage_review(submission_id);
create index if not exists idx_school_moderator_school on school_moderator(school_id);