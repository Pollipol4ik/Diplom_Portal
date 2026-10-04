--liquibase formatted sql

-- =============================================================================
-- Начальные данные: 8 курсов проектной деятельности ИТ-классов МосПолитех 2024
-- БЕЗ PL/pgSQL (Liquibase не поддерживает $$ dollar-quoting).
-- Используем только чистый SQL: INSERT ... SELECT ... WHERE NOT EXISTS.
-- =============================================================================

--changeset Diplom_Backend:2026-05-06-01-seed-course-3d splitStatements:true endDelimiter:;
INSERT INTO course (name, description, is_active, for_lagging_students, created_at)
SELECT '3D-моделирование, 3D-печать и VR/AR-технологии',
       'Программа проектной деятельности по направлению «3D-моделирование, 3D-печать и VR/AR-технологии» для 10 класса ИТ-класс в московской школе. Трудоёмкость: 36 часов.',
       TRUE, FALSE, now()
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = '3D-моделирование, 3D-печать и VR/AR-технологии');

--changeset Diplom_Backend:2026-05-06-02-seed-course-innov splitStatements:true endDelimiter:;
INSERT INTO course (name, description, is_active, for_lagging_students, created_at)
SELECT 'Инновации умного города. Умная школа',
       'Программа проектной деятельности по направлению «Инновации умного города. Умная школа» для 10 класса ИТ-класс в московской школе. Трудоёмкость: 36 часов.',
       TRUE, FALSE, now()
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Инновации умного города. Умная школа');

--changeset Diplom_Backend:2026-05-06-03-seed-course-cyber splitStatements:true endDelimiter:;
INSERT INTO course (name, description, is_active, for_lagging_students, created_at)
SELECT 'Киберфизические системы',
       'Программа проектной деятельности по направлению «Киберфизические системы» (интернет вещей, мобильная робототехника) для 10 класса. Трудоёмкость: 30 часов.',
       TRUE, FALSE, now()
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Киберфизические системы');

--changeset Diplom_Backend:2026-05-06-04-seed-course-mobile splitStatements:true endDelimiter:;
INSERT INTO course (name, description, is_active, for_lagging_students, created_at)
SELECT 'Мобильная разработка и разработка игр',
       'Программа проектной деятельности по направлению «Мобильная разработка и разработка игр» для 10 класса. Трудоёмкость: 30 часов.',
       TRUE, FALSE, now()
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Мобильная разработка и разработка игр');

--changeset Diplom_Backend:2026-05-06-05-seed-course-prog splitStatements:true endDelimiter:;
INSERT INTO course (name, description, is_active, for_lagging_students, created_at)
SELECT 'Программирование. Разработка программ, приложений, веб-сайтов',
       'Программа проектной деятельности по направлению «Программирование. Разработка программ, приложений, веб-сайтов» для 10 класса. Трудоёмкость: 36 часов.',
       TRUE, FALSE, now()
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Программирование. Разработка программ, приложений, веб-сайтов');

--changeset Diplom_Backend:2026-05-06-06-seed-course-biz splitStatements:true endDelimiter:;
INSERT INTO course (name, description, is_active, for_lagging_students, created_at)
SELECT 'Разработка бизнес приложений',
       'Программа проектной деятельности по направлению «Разработка бизнес приложений» для 10 класса. Трудоёмкость: 30 часов.',
       TRUE, FALSE, now()
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Разработка бизнес приложений');

--changeset Diplom_Backend:2026-05-06-07-seed-course-net splitStatements:true endDelimiter:;
INSERT INTO course (name, description, is_active, for_lagging_students, created_at)
SELECT 'Сетевые технологии',
       'Программа проектной деятельности по направлению «Сетевые технологии» для 10 класса. Трудоёмкость: 30 часов.',
       TRUE, FALSE, now()
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Сетевые технологии');

--changeset Diplom_Backend:2026-05-06-08-seed-course-socio splitStatements:true endDelimiter:;
INSERT INTO course (name, description, is_active, for_lagging_students, created_at)
SELECT 'Цифровые технологии в социокультурной сфере',
       'Программа проектной деятельности по направлению «Цифровые технологии в социокультурной сфере» для 10 класса. Трудоёмкость: 36 часов.',
       TRUE, FALSE, now()
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Цифровые технологии в социокультурной сфере');

-- =============================================================================
-- Уроки для курса "3D-моделирование, 3D-печать и VR/AR-технологии" (36 ч)
-- =============================================================================

--changeset Diplom_Backend:2026-05-06-09-lessons-3d splitStatements:true endDelimiter:;
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Выбор и согласование темы проекта',
       'Основные этапы создания проекта. Принципы выбора темы ИТ-проекта по 3D-моделированию, 3D-печати и VR/AR-технологиям. Техники генерации идей.',
       'Сформулируйте название и краткое описание темы вашего проекта. Заявка будет рассмотрена преподавателем.',
       'TEXT', 1, 10, 'HEARING', 'TOPIC_APPROVAL', TRUE
FROM course c WHERE c.name = '3D-моделирование, 3D-печать и VR/AR-технологии'
                AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 1);

INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Анализ предметной области',
       'Принципы поиска и отбора источников информации в области 3D-моделирования и VR/AR.',
       'Проведите анализ предметной области. Опишите актуальность темы, подберите источники, изучите существующие технологии.',
       'TEXT_AND_FILE', 2, 10, 'LESSON', NULL, TRUE
FROM course c WHERE c.name = '3D-моделирование, 3D-печать и VR/AR-технологии'
                AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 2);

INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Анализ аналогов и конкурентов',
       'CustDev и CJM. Методы исследования целевой аудитории.',
       'Найдите и проанализируйте аналоги вашего проекта. Составьте сравнительную таблицу.',
       'TEXT_AND_FILE', 3, 10, 'LESSON', NULL, TRUE
FROM course c WHERE c.name = '3D-моделирование, 3D-печать и VR/AR-технологии'
                AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 3);

INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Проектирование ИТ проекта',
       'Принципы и инструменты для проектирования интерфейса, разработки дизайн-решения, проектирования бизнес-процессов.',
       'Разработайте структуру проекта, создайте прототип или схемы. Загрузите материалы проектирования.',
       'TEXT_AND_FILE', 4, 10, 'LESSON', NULL, TRUE
FROM course c WHERE c.name = '3D-моделирование, 3D-печать и VR/AR-технологии'
                AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 4);

INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Разработка ИТ проекта',
       'Инструментальные средства разработки прототипов ИТ проектов в области 3D-моделирования.',
       'Разработайте прототип вашего ИТ-проекта. Загрузите рабочую версию с описанием.',
       'FILE', 5, 15, 'LESSON', NULL, TRUE
FROM course c WHERE c.name = '3D-моделирование, 3D-печать и VR/AR-технологии'
                AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 5);

INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Промежуточный показ',
       'Предзащита проектов. Требования к промежуточной презентации.',
       'Представьте текущее состояние проекта. Загрузите презентацию и демонстрационные материалы для промежуточной защиты.',
       'FILE', 6, 20, 'HEARING', 'INTERMEDIATE', FALSE
FROM course c WHERE c.name = '3D-моделирование, 3D-печать и VR/AR-технологии'
                AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 6);

INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Оформление пояснительной записки',
       'Принципы оформления технической документации по проекту.',
       'Оформите пояснительную записку к проекту согласно требованиям. Загрузите готовый документ.',
       'FILE', 7, 10, 'LESSON', NULL, TRUE
FROM course c WHERE c.name = '3D-моделирование, 3D-печать и VR/AR-технологии'
                AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 7);

INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Презентация ИТ проекта',
       'Требования к финальной защите проекта. Структура презентации.',
       'Подготовьте финальную презентацию проекта. Загрузите слайды и все сопроводительные материалы.',
       'FILE', 8, 15, 'LESSON', NULL, TRUE
FROM course c WHERE c.name = '3D-моделирование, 3D-печать и VR/AR-технологии'
                AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 8);

INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Защита проекта (итоговая аттестация)',
       'Итоговая защита проектов перед комиссией. Критерии оценивания.',
       'Защитите ваш проект. Загрузите финальную версию всех материалов: презентацию, пояснительную записку и демо.',
       'FILE', 9, 30, 'HEARING', 'FINAL', FALSE
FROM course c WHERE c.name = '3D-моделирование, 3D-печать и VR/AR-технологии'
                AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 9);

-- =============================================================================
-- Уроки для курса "Инновации умного города. Умная школа" (36 ч)
-- =============================================================================

--changeset Diplom_Backend:2026-05-06-10-lessons-innov splitStatements:true endDelimiter:;
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Выбор и согласование темы проекта',
       'Основные этапы создания проекта в области инноваций умного города. Концепция Smart City и Умная школа.',
       'Сформулируйте название и описание вашей инновационной идеи для умного города или школы. Заявка рассматривается преподавателем.',
       'TEXT', 1, 10, 'HEARING', 'TOPIC_APPROVAL', TRUE
FROM course c WHERE c.name = 'Инновации умного города. Умная школа'
                AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 1);

INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Анализ предметной области',
       'Принципы поиска источников в области городских инноваций и Smart City.',
       'Проведите анализ предметной области. Опишите проблему, которую решает ваш проект, и её актуальность для умного города.',
       'TEXT_AND_FILE', 2, 10, 'LESSON', NULL, TRUE
FROM course c WHERE c.name = 'Инновации умного города. Умная школа'
                AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 2);

INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Анализ аналогов и конкурентов',
       'CustDev и CJM. Изучение аналогов в области Smart City.',
       'Найдите и проанализируйте существующие решения в области умного города. Составьте сравнение.',
       'TEXT_AND_FILE', 3, 10, 'LESSON', NULL, TRUE
FROM course c WHERE c.name = 'Инновации умного города. Умная школа'
                AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 3);

INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Проектирование ИТ проекта',
       'Принципы проектирования интерфейса и бизнес-процессов для решений Smart City.',
       'Разработайте архитектуру или прототип вашего инновационного решения. Загрузите схемы и описание.',
       'TEXT_AND_FILE', 4, 10, 'LESSON', NULL, TRUE
FROM course c WHERE c.name = 'Инновации умного города. Умная школа'
                AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 4);

INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Разработка ИТ проекта',
       'Инструментальные средства разработки ИТ прототипов.',
       'Разработайте рабочий прототип вашего инновационного решения. Загрузите материалы с описанием реализации.',
       'FILE', 5, 15, 'LESSON', NULL, TRUE
FROM course c WHERE c.name = 'Инновации умного города. Умная школа'
                AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 5);

INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Промежуточный показ',
       'Предзащита проектов.',
       'Представьте текущее состояние проекта. Загрузите презентацию для промежуточного показа.',
       'FILE', 6, 20, 'HEARING', 'INTERMEDIATE', FALSE
FROM course c WHERE c.name = 'Инновации умного города. Умная школа'
                AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 6);

INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Оформление пояснительной записки',
       'Принципы оформления технической документации.',
       'Оформите пояснительную записку к проекту. Загрузите готовый документ.',
       'FILE', 7, 10, 'LESSON', NULL, TRUE
FROM course c WHERE c.name = 'Инновации умного города. Умная школа'
                AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 7);

INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Презентация ИТ проекта',
       'Требования к финальной защите.',
       'Подготовьте финальную презентацию. Загрузите слайды и сопроводительные материалы.',
       'FILE', 8, 15, 'LESSON', NULL, TRUE
FROM course c WHERE c.name = 'Инновации умного города. Умная школа'
                AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 8);

INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Защита проекта (итоговая аттестация)',
       'Итоговая защита перед комиссией.',
       'Защитите ваш проект. Загрузите финальную версию всех материалов.',
       'FILE', 9, 30, 'HEARING', 'FINAL', FALSE
FROM course c WHERE c.name = 'Инновации умного города. Умная школа'
                AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 9);

-- =============================================================================
-- Уроки для остальных 6 курсов (30 и 36 ч — структура уроков одинакова)
-- Генерируем одним INSERT на урок для каждого курса
-- =============================================================================

--changeset Diplom_Backend:2026-05-06-11-lessons-rest splitStatements:true endDelimiter:;

-- ── Киберфизические системы (30 ч) ──────────────────────────────────────────
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Выбор и согласование темы проекта', 'Основные этапы создания проекта в области киберфизических систем, IoT и мобильной робототехники.', 'Сформулируйте тему вашего проекта в области киберфизических систем. Заявка рассматривается преподавателем.', 'TEXT', 1, 10, 'HEARING', 'TOPIC_APPROVAL', TRUE FROM course c WHERE c.name = 'Киберфизические системы' AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 1);
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Анализ предметной области', 'Принципы поиска источников в области IoT и робототехники.', 'Проведите анализ предметной области вашего проекта.', 'TEXT_AND_FILE', 2, 10, 'LESSON', NULL, TRUE FROM course c WHERE c.name = 'Киберфизические системы' AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 2);
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Анализ аналогов и конкурентов', 'CustDev и CJM в контексте IoT-решений.', 'Проанализируйте существующие решения в области вашего проекта.', 'TEXT_AND_FILE', 3, 10, 'LESSON', NULL, TRUE FROM course c WHERE c.name = 'Киберфизические системы' AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 3);
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Проектирование ИТ проекта', 'Принципы проектирования архитектуры киберфизических систем.', 'Разработайте архитектуру и схемы вашего проекта.', 'TEXT_AND_FILE', 4, 10, 'LESSON', NULL, TRUE FROM course c WHERE c.name = 'Киберфизические системы' AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 4);
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Разработка ИТ проекта', 'Инструменты разработки IoT-прототипов.', 'Разработайте прототип вашего проекта. Загрузите результаты.', 'FILE', 5, 15, 'LESSON', NULL, TRUE FROM course c WHERE c.name = 'Киберфизические системы' AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 5);
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Промежуточный показ', 'Предзащита проектов.', 'Представьте текущее состояние проекта. Загрузите презентацию.', 'FILE', 6, 20, 'HEARING', 'INTERMEDIATE', FALSE FROM course c WHERE c.name = 'Киберфизические системы' AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 6);
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Оформление пояснительной записки', 'Принципы оформления технической документации.', 'Оформите пояснительную записку к проекту.', 'FILE', 7, 10, 'LESSON', NULL, TRUE FROM course c WHERE c.name = 'Киберфизические системы' AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 7);
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Презентация ИТ проекта', 'Требования к финальной защите.', 'Подготовьте финальную презентацию проекта.', 'FILE', 8, 15, 'LESSON', NULL, TRUE FROM course c WHERE c.name = 'Киберфизические системы' AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 8);
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Защита проекта (итоговая аттестация)', 'Итоговая защита перед комиссией.', 'Загрузите финальную версию всех материалов проекта.', 'FILE', 9, 30, 'HEARING', 'FINAL', FALSE FROM course c WHERE c.name = 'Киберфизические системы' AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 9);

-- ── Мобильная разработка и разработка игр (30 ч) ────────────────────────────
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Выбор и согласование темы проекта', 'Основные этапы создания мобильного приложения или игры.', 'Сформулируйте тему вашего проекта в области мобильной разработки или игр.', 'TEXT', 1, 10, 'HEARING', 'TOPIC_APPROVAL', TRUE FROM course c WHERE c.name = 'Мобильная разработка и разработка игр' AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 1);
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Анализ предметной области', 'Принципы поиска источников в области мобильной разработки и геймдева.', 'Проведите анализ рынка мобильных приложений или игр по теме проекта.', 'TEXT_AND_FILE', 2, 10, 'LESSON', NULL, TRUE FROM course c WHERE c.name = 'Мобильная разработка и разработка игр' AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 2);
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Анализ аналогов и конкурентов', 'CustDev и CJM для мобильных приложений и игр.', 'Проанализируйте аналогичные приложения или игры на рынке.', 'TEXT_AND_FILE', 3, 10, 'LESSON', NULL, TRUE FROM course c WHERE c.name = 'Мобильная разработка и разработка игр' AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 3);
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Проектирование ИТ проекта', 'Проектирование UI/UX мобильного приложения или игровой механики.', 'Разработайте макеты интерфейса или игровые механики. Загрузите прототипы.', 'TEXT_AND_FILE', 4, 10, 'LESSON', NULL, TRUE FROM course c WHERE c.name = 'Мобильная разработка и разработка игр' AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 4);
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Разработка ИТ проекта', 'Инструменты мобильной разработки и геймдева.', 'Разработайте рабочую версию приложения или игры. Загрузите APK, ссылку или скриншоты.', 'FILE', 5, 15, 'LESSON', NULL, TRUE FROM course c WHERE c.name = 'Мобильная разработка и разработка игр' AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 5);
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Промежуточный показ', 'Предзащита проектов.', 'Представьте демо вашего приложения или игры. Загрузите презентацию.', 'FILE', 6, 20, 'HEARING', 'INTERMEDIATE', FALSE FROM course c WHERE c.name = 'Мобильная разработка и разработка игр' AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 6);
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Оформление пояснительной записки', 'Принципы оформления технической документации.', 'Оформите пояснительную записку к проекту.', 'FILE', 7, 10, 'LESSON', NULL, TRUE FROM course c WHERE c.name = 'Мобильная разработка и разработка игр' AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 7);
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Презентация ИТ проекта', 'Требования к финальной защите.', 'Подготовьте финальную презентацию проекта.', 'FILE', 8, 15, 'LESSON', NULL, TRUE FROM course c WHERE c.name = 'Мобильная разработка и разработка игр' AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 8);
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Защита проекта (итоговая аттестация)', 'Итоговая защита перед комиссией.', 'Загрузите финальную версию всех материалов проекта.', 'FILE', 9, 30, 'HEARING', 'FINAL', FALSE FROM course c WHERE c.name = 'Мобильная разработка и разработка игр' AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 9);

-- ── Программирование (36 ч) ──────────────────────────────────────────────────
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Выбор и согласование темы проекта', 'Основные этапы создания программного продукта, веб-приложения или сайта.', 'Сформулируйте тему вашего проекта в области программирования или веб-разработки.', 'TEXT', 1, 10, 'HEARING', 'TOPIC_APPROVAL', TRUE FROM course c WHERE c.name = 'Программирование. Разработка программ, приложений, веб-сайтов' AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 1);
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Анализ предметной области', 'Принципы выбора технологического стека и анализ предметной области.', 'Проведите анализ предметной области и выбор технологий для вашего проекта.', 'TEXT_AND_FILE', 2, 10, 'LESSON', NULL, TRUE FROM course c WHERE c.name = 'Программирование. Разработка программ, приложений, веб-сайтов' AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 2);
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Анализ аналогов и конкурентов', 'CustDev и CJM. Обзор конкурирующих программных решений.', 'Проанализируйте аналогичные программные продукты и сайты.', 'TEXT_AND_FILE', 3, 10, 'LESSON', NULL, TRUE FROM course c WHERE c.name = 'Программирование. Разработка программ, приложений, веб-сайтов' AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 3);
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Проектирование ИТ проекта', 'Проектирование архитектуры, базы данных, UX/UI.', 'Разработайте техническое задание, ERD-схему и макеты интерфейса.', 'TEXT_AND_FILE', 4, 10, 'LESSON', NULL, TRUE FROM course c WHERE c.name = 'Программирование. Разработка программ, приложений, веб-сайтов' AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 4);
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Разработка ИТ проекта', 'Инструменты разработки программ и веб-приложений.', 'Разработайте рабочую версию продукта. Загрузите ссылку на репозиторий или архив с кодом.', 'FILE', 5, 15, 'LESSON', NULL, TRUE FROM course c WHERE c.name = 'Программирование. Разработка программ, приложений, веб-сайтов' AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 5);
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Промежуточный показ', 'Предзащита проектов.', 'Продемонстрируйте текущий прогресс разработки. Загрузите презентацию.', 'FILE', 6, 20, 'HEARING', 'INTERMEDIATE', FALSE FROM course c WHERE c.name = 'Программирование. Разработка программ, приложений, веб-сайтов' AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 6);
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Оформление пояснительной записки', 'Принципы оформления технической документации.', 'Оформите пояснительную записку. Загрузите готовый документ.', 'FILE', 7, 10, 'LESSON', NULL, TRUE FROM course c WHERE c.name = 'Программирование. Разработка программ, приложений, веб-сайтов' AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 7);
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Презентация ИТ проекта', 'Требования к финальной защите.', 'Подготовьте финальную презентацию. Загрузите слайды и сопроводительные материалы.', 'FILE', 8, 15, 'LESSON', NULL, TRUE FROM course c WHERE c.name = 'Программирование. Разработка программ, приложений, веб-сайтов' AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 8);
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Защита проекта (итоговая аттестация)', 'Итоговая защита перед комиссией.', 'Загрузите финальную версию всех материалов: код, презентацию, пояснительную записку.', 'FILE', 9, 30, 'HEARING', 'FINAL', FALSE FROM course c WHERE c.name = 'Программирование. Разработка программ, приложений, веб-сайтов' AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 9);

-- ── Разработка бизнес приложений (30 ч) ─────────────────────────────────────
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Выбор и согласование темы проекта', 'Основные этапы создания бизнес-приложения. Понятие корпоративного ПО.', 'Сформулируйте тему вашего бизнес-приложения и его целевую аудиторию.', 'TEXT', 1, 10, 'HEARING', 'TOPIC_APPROVAL', TRUE FROM course c WHERE c.name = 'Разработка бизнес приложений' AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 1);
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Анализ предметной области', 'Принципы анализа бизнес-процессов и требований к ПО.', 'Опишите бизнес-процессы, которые автоматизирует ваше приложение.', 'TEXT_AND_FILE', 2, 10, 'LESSON', NULL, TRUE FROM course c WHERE c.name = 'Разработка бизнес приложений' AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 2);
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Анализ аналогов и конкурентов', 'CustDev и CJM для бизнес-приложений.', 'Проанализируйте существующие бизнес-приложения в вашей нише.', 'TEXT_AND_FILE', 3, 10, 'LESSON', NULL, TRUE FROM course c WHERE c.name = 'Разработка бизнес приложений' AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 3);
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Проектирование ИТ проекта', 'Проектирование архитектуры бизнес-приложения и бизнес-процессов.', 'Разработайте архитектуру, схему базы данных и прототип интерфейса.', 'TEXT_AND_FILE', 4, 10, 'LESSON', NULL, TRUE FROM course c WHERE c.name = 'Разработка бизнес приложений' AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 4);
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Разработка ИТ проекта', 'Инструменты разработки корпоративных приложений.', 'Разработайте рабочий прототип бизнес-приложения. Загрузите результаты.', 'FILE', 5, 15, 'LESSON', NULL, TRUE FROM course c WHERE c.name = 'Разработка бизнес приложений' AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 5);
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Промежуточный показ', 'Предзащита проектов.', 'Представьте демо вашего бизнес-приложения. Загрузите презентацию.', 'FILE', 6, 20, 'HEARING', 'INTERMEDIATE', FALSE FROM course c WHERE c.name = 'Разработка бизнес приложений' AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 6);
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Оформление пояснительной записки', 'Принципы оформления технической документации.', 'Оформите пояснительную записку к проекту.', 'FILE', 7, 10, 'LESSON', NULL, TRUE FROM course c WHERE c.name = 'Разработка бизнес приложений' AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 7);
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Презентация ИТ проекта', 'Требования к финальной защите.', 'Подготовьте финальную презентацию проекта.', 'FILE', 8, 15, 'LESSON', NULL, TRUE FROM course c WHERE c.name = 'Разработка бизнес приложений' AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 8);
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Защита проекта (итоговая аттестация)', 'Итоговая защита перед комиссией.', 'Загрузите финальную версию всех материалов проекта.', 'FILE', 9, 30, 'HEARING', 'FINAL', FALSE FROM course c WHERE c.name = 'Разработка бизнес приложений' AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 9);

-- ── Сетевые технологии (30 ч) ────────────────────────────────────────────────
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Выбор и согласование темы проекта', 'Основные этапы создания проекта в области сетевых технологий.', 'Сформулируйте тему вашего проекта в области сетевых технологий.', 'TEXT', 1, 10, 'HEARING', 'TOPIC_APPROVAL', TRUE FROM course c WHERE c.name = 'Сетевые технологии' AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 1);
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Анализ предметной области', 'Принципы анализа в области сетевых технологий.', 'Проведите анализ предметной области вашего проекта.', 'TEXT_AND_FILE', 2, 10, 'LESSON', NULL, TRUE FROM course c WHERE c.name = 'Сетевые технологии' AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 2);
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Анализ аналогов и конкурентов', 'CustDev и CJM в контексте сетевых решений.', 'Проанализируйте существующие решения в области сетевых технологий.', 'TEXT_AND_FILE', 3, 10, 'LESSON', NULL, TRUE FROM course c WHERE c.name = 'Сетевые технологии' AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 3);
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Проектирование ИТ проекта', 'Проектирование сетевой архитектуры и топологии.', 'Разработайте схему сетевой архитектуры и описание протоколов.', 'TEXT_AND_FILE', 4, 10, 'LESSON', NULL, TRUE FROM course c WHERE c.name = 'Сетевые технологии' AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 4);
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Разработка ИТ проекта', 'Инструменты реализации сетевых проектов.', 'Реализуйте прототип вашего сетевого решения. Загрузите результаты.', 'FILE', 5, 15, 'LESSON', NULL, TRUE FROM course c WHERE c.name = 'Сетевые технологии' AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 5);
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Промежуточный показ', 'Предзащита проектов.', 'Продемонстрируйте текущий прогресс. Загрузите презентацию.', 'FILE', 6, 20, 'HEARING', 'INTERMEDIATE', FALSE FROM course c WHERE c.name = 'Сетевые технологии' AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 6);
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Оформление пояснительной записки', 'Принципы оформления технической документации.', 'Оформите пояснительную записку к проекту.', 'FILE', 7, 10, 'LESSON', NULL, TRUE FROM course c WHERE c.name = 'Сетевые технологии' AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 7);
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Презентация ИТ проекта', 'Требования к финальной защите.', 'Подготовьте финальную презентацию проекта.', 'FILE', 8, 15, 'LESSON', NULL, TRUE FROM course c WHERE c.name = 'Сетевые технологии' AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 8);
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Защита проекта (итоговая аттестация)', 'Итоговая защита перед комиссией.', 'Загрузите финальную версию всех материалов проекта.', 'FILE', 9, 30, 'HEARING', 'FINAL', FALSE FROM course c WHERE c.name = 'Сетевые технологии' AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 9);

-- ── Цифровые технологии в социокультурной сфере (36 ч) ──────────────────────
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Выбор и согласование темы проекта', 'Основные этапы создания ИТ-проекта в социокультурной сфере. Цифровые технологии в культуре, образовании, искусстве.', 'Сформулируйте тему вашего проекта в области цифровых технологий для социокультурной сферы.', 'TEXT', 1, 10, 'HEARING', 'TOPIC_APPROVAL', TRUE FROM course c WHERE c.name = 'Цифровые технологии в социокультурной сфере' AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 1);
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Анализ предметной области', 'Принципы анализа социокультурной сферы для ИТ-проектов.', 'Проведите анализ социокультурного контекста и аудитории вашего проекта.', 'TEXT_AND_FILE', 2, 10, 'LESSON', NULL, TRUE FROM course c WHERE c.name = 'Цифровые технологии в социокультурной сфере' AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 2);
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Анализ аналогов и конкурентов', 'CustDev и CJM для социокультурных проектов.', 'Проанализируйте аналогичные цифровые проекты в социокультурной сфере.', 'TEXT_AND_FILE', 3, 10, 'LESSON', NULL, TRUE FROM course c WHERE c.name = 'Цифровые технологии в социокультурной сфере' AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 3);
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Проектирование ИТ проекта', 'Проектирование интерфейса и пользовательского опыта для социокультурных проектов.', 'Разработайте прототип и UI/UX концепцию вашего проекта.', 'TEXT_AND_FILE', 4, 10, 'LESSON', NULL, TRUE FROM course c WHERE c.name = 'Цифровые технологии в социокультурной сфере' AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 4);
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Разработка ИТ проекта', 'Инструменты создания цифровых продуктов для социокультурной сферы.', 'Разработайте рабочий прототип или MVP вашего проекта. Загрузите результаты.', 'FILE', 5, 15, 'LESSON', NULL, TRUE FROM course c WHERE c.name = 'Цифровые технологии в социокультурной сфере' AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 5);
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Промежуточный показ', 'Предзащита проектов.', 'Представьте промежуточные результаты. Загрузите презентацию.', 'FILE', 6, 20, 'HEARING', 'INTERMEDIATE', FALSE FROM course c WHERE c.name = 'Цифровые технологии в социокультурной сфере' AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 6);
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Оформление пояснительной записки', 'Принципы оформления технической документации.', 'Оформите пояснительную записку к проекту.', 'FILE', 7, 10, 'LESSON', NULL, TRUE FROM course c WHERE c.name = 'Цифровые технологии в социокультурной сфере' AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 7);
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Презентация ИТ проекта', 'Требования к финальной защите.', 'Подготовьте финальную презентацию проекта.', 'FILE', 8, 15, 'LESSON', NULL, TRUE FROM course c WHERE c.name = 'Цифровые технологии в социокультурной сфере' AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 8);
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students)
SELECT c.id, 'Защита проекта (итоговая аттестация)', 'Итоговая защита перед комиссией.', 'Загрузите финальную версию всех материалов проекта.', 'FILE', 9, 30, 'HEARING', 'FINAL', FALSE FROM course c WHERE c.name = 'Цифровые технологии в социокультурной сфере' AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 9);