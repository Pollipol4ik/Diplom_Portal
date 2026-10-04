--changeset Polina Kuptsova:14-add-moderator-role-and-subject-moderator-table
insert into role (name) values ('ROLE_MODERATOR');

create sequence if not exists subject_moderator_pk_seq start 1 increment 1;
create table if not exists subject_moderator (
                                                 id bigint primary key default nextval('subject_moderator_pk_seq'),
                                                 account_id bigint not null references account(id) on delete cascade,
                                                 subject_id bigint not null references subject(id) on delete cascade,
                                                 assigned_at timestamp not null default now()
);


alter table subject_moderator add constraint subject_moderator_unq unique (account_id, subject_id);