--liquibase formatted sql

--changeset Polina Kuptsova:1-create-direction-table
create sequence if not exists direction_pk_seq start 1 increment 1;
create table if not exists direction (
                                         id bigint primary key default nextval('direction_pk_seq'),
                                         name text not null check ( length(name) > 0 )
);

--changeset Polina Kuptsova:2-create-subject-table
create sequence if not exists subject_pk_seq start 1 increment 1;
create table if not exists subject (
                                       id bigint primary key default nextval('subject_pk_seq'),
                                       name text not null check ( length(name) > 0 ),
                                       direction_id bigint references direction(id) not null
);
alter table subject add constraint subject_unq unique (name, direction_id);

--changeset Polina Kuptsova:3-create-topic-type-table
create sequence if not exists topic_type_pk_seq start 1 increment 1;
create table if not exists topic_type (
                                          id bigint primary key default nextval('topic_type_pk_seq'),
                                          type int check ( type >= 0 )
);

--changeset Polina Kuptsova:4-create-subject-topic-table
create sequence if not exists subject_topic_pk_seq start 1 increment 1;
create table if not exists subject_topic (
                                             id bigint primary key default nextval('subject_topic_pk_seq'),
                                             type_id bigint references topic_type(id) not null,
                                             subject_id bigint references subject(id) not null
);

--changeset Polina Kuptsova:5-create-school-and-class-tables
create sequence if not exists school_pk_seq start 1 increment 1;
create table if not exists school (
                                      id bigint primary key default nextval('school_pk_seq'),
                                      name text not null unique check ( length(name) > 0 )
);

create sequence if not exists school_class_pk_seq start 1 increment 1;
create table if not exists school_class (
                                            id bigint primary key default nextval('school_class_pk_seq'),
                                            name text not null check ( length(name) > 0 ),
                                            school_id bigint references school(id) not null
);

--changeset Polina Kuptsova:create-roles-table
create sequence if not exists role_pk_seq start 1 increment 1;
create table if not exists role (
                                    id bigint primary key default nextval('role_pk_seq'),
                                    name text not null check ( length(name) > 0 )
);

--changeset Polina Kuptsova:9-create-file-table
create sequence if not exists file_pk_seq start 1 increment 1;
create table if not exists file (
                                    id bigint primary key default nextval('file_pk_seq'),
                                    file_name_in_directory text not null check ( length(file_name_in_directory) > 0 ),
                                    initial_file_name text not null check ( length(initial_file_name) > 0 )
);

--changeset Polina_Kuptsova:6-create-final-account-table
create sequence if not exists account_pk_seq start 1 increment 1;
create table if not exists account (
                                       id bigint primary key default nextval('account_pk_seq'),
                                       email text not null unique check ( length(email) > 0 ),
                                       password text not null check (length(password) > 0 ),
                                       nickname text unique check (length(nickname) > 0),
                                       first_name text check ( length(first_name) > 0 ),
                                       last_name text check ( length(last_name) > 0 ),
                                       middle_name text check ( length(middle_name) > 0 ),
                                       birth_date date,
                                       description text,
                                       is_banned bool not null default false,
                                       role_id bigint not null references role(id),
                                       school_id bigint references school(id),
                                       class_id bigint references school_class(id),
                                       photo_id bigint references file(id)
);

--changeset Polina Kuptsova:7-create-publication-table
create sequence if not exists publication_pk_seq start 1 increment 1;
create table if not exists publication (
                                           id bigint primary key default nextval('publication_pk_seq'),
                                           title text not null,
                                           description text not null,
                                           account_id bigint references account(id) not null,
                                           supports_thread bool not null default false,
                                           created_at timestamp not null
);


--changeset Polina Kuptsova:8-create-news_publication-table
create table news_publication (
                                  id bigint primary key references publication(id)
);



--changeset Polina Kuptsova:10-link-account-and-publications-to-files
alter table account add constraint fk_account_photo foreign key (photo_id) references file(id);

CREATE TABLE publication_file (
                                  publication_id BIGINT REFERENCES publication (id),
                                  file_id        BIGINT REFERENCES file (id),
                                  PRIMARY KEY (publication_id, file_id)
);

--changeset Polina Kuptsova:11-create-comment-table
create sequence if not exists comment_pk_seq start 1 increment 1;
create table if not exists comment (
                                       id bigint primary key default nextval('comment_pk_seq'),
                                       content text not null check ( length(content) > 0 ),
                                       publication_id bigint references publication(id) not null,
                                       account_id bigint references account(id) not null,
                                       parent bigint references comment(id),
                                       is_anonymous bool not null default false,
                                       created_at timestamp not null,
                                       last_updated_at timestamp not null default now()
);

--changeset Polina Kuptsova:12-create-subject_topic_publication-table
CREATE TABLE subject_topic_publication (
                                           publication_id BIGINT REFERENCES publication (id),
                                           subject_topic_id BIGINT REFERENCES subject_topic(id),
                                           PRIMARY KEY (publication_id, subject_topic_id)
);




--changeset Polina Kuptsova:13-insert-initial-data
insert into role (name) values ('ROLE_USER'), ('ROLE_ADMIN');
insert into school (name) values ('Гимназия №1'), ('Лицей №10');
insert into school_class (name, school_id) values ('10-А', 1), ('11-Б', 2);
insert into direction (name) values ('Инженерная школа'), ('Школа цифровой экономики');
insert into subject (name, direction_id) values ('МАТЕМАТИКА', 1), ('ПРОГРАММИРОВАНИЕ', 2);
