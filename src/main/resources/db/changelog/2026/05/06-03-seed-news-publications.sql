--liquibase formatted sql

-- =============================================================================
-- Начальные данные: новостные публикации.
-- Каждая новость — запись в publication + запись в news_publication (тот же id).
-- supports_thread = true (комментарии включены).
-- account_id = 1 — системный аккаунт администратора.
-- Все INSERT используют WHERE NOT EXISTS для идемпотентности.
-- =============================================================================

-- ── 1. Добро пожаловать ───────────────────────────────────────────────────────

--changeset Diplom_Backend:2026-06-03-01-news-welcome splitStatements:true endDelimiter:;
INSERT INTO publication (title, description, account_id, supports_thread, created_at)
SELECT
    'Добро пожаловать на платформу проектной деятельности!',
    'Рады приветствовать вас на платформе управления проектной деятельностью ИТ-классов МосПолитех. Здесь вы можете выбрать курс, сформировать проектную группу, выбрать и согласовать тему проекта, сдавать работы на каждом этапе и отслеживать свой прогресс. Модераторы проверяют работы и дают обратную связь прямо в системе. Желаем продуктивной работы!',
    1, TRUE, '2025-09-01 09:00:00'
WHERE NOT EXISTS (
    SELECT 1 FROM publication WHERE title = 'Добро пожаловать на платформу проектной деятельности!'
);

--changeset Diplom_Backend:2026-06-03-02-news-welcome-link splitStatements:true endDelimiter:;
INSERT INTO news_publication (id)
SELECT p.id FROM publication p
WHERE p.title = 'Добро пожаловать на платформу проектной деятельности!'
  AND NOT EXISTS (SELECT 1 FROM news_publication np WHERE np.id = p.id);

-- ── 2. Начало учебного года ───────────────────────────────────────────────────

--changeset Diplom_Backend:2026-06-03-03-news-year-start splitStatements:true endDelimiter:;
INSERT INTO publication (title, description, account_id, supports_thread, created_at)
SELECT
    'Старт проектной деятельности 2024–2025',
    'С 9 сентября 2024 года открывается запись на курсы проектной деятельности для учеников ИТ-классов. Каждый ученик должен выбрать направление, сформировать группу и подать тему проекта на согласование до указанного дедлайна. Обратите внимание: ученики, не выбравшие тему в срок, автоматически получают статус отстающего и переводятся на курс индивидуальной поддержки.',
    1, TRUE, '2025-09-09 10:00:00'
WHERE NOT EXISTS (
    SELECT 1 FROM publication WHERE title = 'Старт проектной деятельности 2024–2025'
);

--changeset Diplom_Backend:2026-06-03-04-news-year-start-link splitStatements:true endDelimiter:;
INSERT INTO news_publication (id)
SELECT p.id FROM publication p
WHERE p.title = 'Старт проектной деятельности 2024–2025'
  AND NOT EXISTS (SELECT 1 FROM news_publication np WHERE np.id = p.id);

-- ── 3. Требования к пояснительной записке ─────────────────────────────────────

--changeset Diplom_Backend:2026-06-03-05-news-report-requirements splitStatements:true endDelimiter:;
INSERT INTO publication (title, description, account_id, supports_thread, created_at)
SELECT
    'Требования к оформлению пояснительной записки',
    'Опубликованы обновлённые требования к оформлению пояснительных записок к проектам. Документ должен содержать: титульный лист, содержание, введение с обоснованием актуальности, основную часть с описанием реализации, заключение с выводами и список использованных источников. Шаблон оформления и чек-лист доступны в разделе «Учебные материалы → Методические материалы».',
    1, TRUE, '2025-10-01 11:00:00'
WHERE NOT EXISTS (
    SELECT 1 FROM publication WHERE title = 'Требования к оформлению пояснительной записки'
);

--changeset Diplom_Backend:2026-06-03-06-news-report-requirements-link splitStatements:true endDelimiter:;
INSERT INTO news_publication (id)
SELECT p.id FROM publication p
WHERE p.title = 'Требования к оформлению пояснительной записки'
  AND NOT EXISTS (SELECT 1 FROM news_publication np WHERE np.id = p.id);

-- ── 4. Дедлайн выбора тем ────────────────────────────────────────────────────

--changeset Diplom_Backend:2026-06-03-07-news-deadline splitStatements:true endDelimiter:;
INSERT INTO publication (title, description, account_id, supports_thread, created_at)
SELECT
    'Напоминание: дедлайн выбора темы проекта — 1 ноября',
    'Напоминаем, что выбор и согласование темы проекта необходимо завершить до 1 ноября 2024 года включительно. Группы, не подавшие тему в срок, будут автоматически помечены как отстающие. Если у вас возникли затруднения с выбором направления — обратитесь к своему модератору или воспользуйтесь банком идей на платформе.',
    1, TRUE, '2025-10-20 09:00:00'
WHERE NOT EXISTS (
    SELECT 1 FROM publication WHERE title = 'Напоминание: дедлайн выбора темы проекта — 1 ноября'
);

--changeset Diplom_Backend:2026-06-03-08-news-deadline-link splitStatements:true endDelimiter:;
INSERT INTO news_publication (id)
SELECT p.id FROM publication p
WHERE p.title = 'Напоминание: дедлайн выбора темы проекта — 1 ноября'
  AND NOT EXISTS (SELECT 1 FROM news_publication np WHERE np.id = p.id);

-- ── 5. Промежуточный показ ────────────────────────────────────────────────────

--changeset Diplom_Backend:2026-06-03-09-news-intermediate splitStatements:true endDelimiter:;
INSERT INTO publication (title, description, account_id, supports_thread, created_at)
SELECT
    'Промежуточный показ проектов: расписание и требования',
    'В декабре 2024 года состоится промежуточный показ проектов. Каждая группа должна представить рабочий прототип и загрузить презентацию в систему до начала показа. Расписание выступлений будет опубликовано модераторами курсов в личных кабинетах групп. Критерии оценивания доступны на странице соответствующего этапа курса.',
    1, TRUE, '2025-11-15 12:00:00'
WHERE NOT EXISTS (
    SELECT 1 FROM publication WHERE title = 'Промежуточный показ проектов: расписание и требования'
);

--changeset Diplom_Backend:2026-06-03-10-news-intermediate-link splitStatements:true endDelimiter:;
INSERT INTO news_publication (id)
SELECT p.id FROM publication p
WHERE p.title = 'Промежуточный показ проектов: расписание и требования'
  AND NOT EXISTS (SELECT 1 FROM news_publication np WHERE np.id = p.id);

-- ── 6. Конференция ───────────────────────────────────────────────────────────

--changeset Diplom_Backend:2026-06-03-11-news-conference splitStatements:true endDelimiter:;
INSERT INTO publication (title, description, account_id, supports_thread, created_at)
SELECT
    'Конференция проектных работ — май 2025',
    'В мае 2025 года состоится итоговая конференция защиты проектных работ ИТ-классов МосПолитех. К участию допускаются группы, успешно прошедшие все предшествующие этапы слушаний. Финальные материалы — презентация и пояснительная записка — должны быть загружены в систему не позднее чем за 5 дней до даты защиты. Результаты оценивания будут доступны на платформе в личном кабинете.',
    1, TRUE, '2026-04-01 10:00:00'
WHERE NOT EXISTS (
    SELECT 1 FROM publication WHERE title = 'Конференция проектных работ — май 2025'
);

--changeset Diplom_Backend:2026-06-03-12-news-conference-link splitStatements:true endDelimiter:;
INSERT INTO news_publication (id)
SELECT p.id FROM publication p
WHERE p.title = 'Конференция проектных работ — май 2025'
  AND NOT EXISTS (SELECT 1 FROM news_publication np WHERE np.id = p.id);