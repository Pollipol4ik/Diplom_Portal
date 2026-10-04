--liquibase formatted sql

-- =============================================================================
-- ФИНАЛЬНАЯ МИГРАЦИЯ: ДАННЫЕ ДЛЯ ЭТАПА CONFERENCE_DEFENSE
--
-- Добавляет:
-- 1. Hearing submissions для CONFERENCE_DEFENSE (с разными статусами)
-- 2. Hearing reviews для проверенных работ (используя существующих модераторов)
--
-- Модераторы уже существуют: ant.vasiliev, pol.kuptsova
-- =============================================================================

-- =============================================================================
-- ЧАСТЬ 1: HEARING_SUBMISSION (только если ещё нет)
-- =============================================================================

-- Группа 1: Чат-мессенджер — ACCEPTED
--changeset Diplom_Backend:2026-06-12-sub-1 splitStatements:true endDelimiter:;
INSERT INTO hearing_submission (lesson_id, group_id, status, current_version, submitted_at, updated_at)
SELECT cl.id, cg.id, 'ACCEPTED', 1, NOW() - INTERVAL '10 days', NOW() - INTERVAL '5 days'
FROM course_lesson cl
         JOIN course c ON cl.course_id = c.id
         JOIN course_group cg ON cg.course_id = c.id
WHERE c.name = 'Сетевые технологии'
  AND cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND cg.title = 'Чат-мессенджер в локальной сети'
  AND NOT EXISTS (SELECT 1 FROM hearing_submission hs WHERE hs.lesson_id = cl.id AND hs.group_id = cg.id);

-- Группа 7: RIP и OSPF — ACCEPTED (версия 2)
--changeset Diplom_Backend:2026-06-12-sub-2 splitStatements:true endDelimiter:;
INSERT INTO hearing_submission (lesson_id, group_id, status, current_version, submitted_at, updated_at)
SELECT cl.id, cg.id, 'ACCEPTED', 2, NOW() - INTERVAL '14 days', NOW() - INTERVAL '3 days'
FROM course_lesson cl
         JOIN course c ON cl.course_id = c.id
         JOIN course_group cg ON cg.course_id = c.id
WHERE c.name = 'Сетевые технологии'
  AND cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND cg.title = 'Протоколы маршрутизации RIP и OSPF'
  AND NOT EXISTS (SELECT 1 FROM hearing_submission hs WHERE hs.lesson_id = cl.id AND hs.group_id = cg.id);

-- Группа 9: Межсетевой экран — ACCEPTED
--changeset Diplom_Backend:2026-06-12-sub-3 splitStatements:true endDelimiter:;
INSERT INTO hearing_submission (lesson_id, group_id, status, current_version, submitted_at, updated_at)
SELECT cl.id, cg.id, 'ACCEPTED', 1, NOW() - INTERVAL '11 days', NOW() - INTERVAL '6 days'
FROM course_lesson cl
         JOIN course c ON cl.course_id = c.id
         JOIN course_group cg ON cg.course_id = c.id
WHERE c.name = 'Сетевые технологии'
  AND cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND cg.title = 'Настройка межсетевого экрана'
  AND NOT EXISTS (SELECT 1 FROM hearing_submission hs WHERE hs.lesson_id = cl.id AND hs.group_id = cg.id);

-- Группа 2: Мониторинг — ON_REVIEW
--changeset Diplom_Backend:2026-06-12-sub-4 splitStatements:true endDelimiter:;
INSERT INTO hearing_submission (lesson_id, group_id, status, current_version, submitted_at, updated_at)
SELECT cl.id, cg.id, 'ON_REVIEW', 1, NOW() - INTERVAL '3 days', NOW() - INTERVAL '3 days'
FROM course_lesson cl
         JOIN course c ON cl.course_id = c.id
         JOIN course_group cg ON cg.course_id = c.id
WHERE c.name = 'Сетевые технологии'
  AND cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND cg.title = 'Система мониторинга сети'
  AND NOT EXISTS (SELECT 1 FROM hearing_submission hs WHERE hs.lesson_id = cl.id AND hs.group_id = cg.id);

-- Группа 5: DNS и DHCP — ON_REVIEW
--changeset Diplom_Backend:2026-06-12-sub-5 splitStatements:true endDelimiter:;
INSERT INTO hearing_submission (lesson_id, group_id, status, current_version, submitted_at, updated_at)
SELECT cl.id, cg.id, 'ON_REVIEW', 1, NOW() - INTERVAL '5 days', NOW() - INTERVAL '5 days'
FROM course_lesson cl
         JOIN course c ON cl.course_id = c.id
         JOIN course_group cg ON cg.course_id = c.id
WHERE c.name = 'Сетевые технологии'
  AND cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND cg.title = 'Настройка DNS и DHCP-сервера'
  AND NOT EXISTS (SELECT 1 FROM hearing_submission hs WHERE hs.lesson_id = cl.id AND hs.group_id = cg.id);

-- Группа 3: Корпоративная сеть — NEEDS_REVISION
--changeset Diplom_Backend:2026-06-12-sub-6 splitStatements:true endDelimiter:;
INSERT INTO hearing_submission (lesson_id, group_id, status, current_version, submitted_at, updated_at)
SELECT cl.id, cg.id, 'NEEDS_REVISION', 1, NOW() - INTERVAL '8 days', NOW() - INTERVAL '7 days'
FROM course_lesson cl
         JOIN course c ON cl.course_id = c.id
         JOIN course_group cg ON cg.course_id = c.id
WHERE c.name = 'Сетевые технологии'
  AND cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND cg.title = 'Настройка корпоративной сети'
  AND NOT EXISTS (SELECT 1 FROM hearing_submission hs WHERE hs.lesson_id = cl.id AND hs.group_id = cg.id);

-- Группа 8: Wi-Fi 802.1X — NEEDS_REVISION
--changeset Diplom_Backend:2026-06-12-sub-7 splitStatements:true endDelimiter:;
INSERT INTO hearing_submission (lesson_id, group_id, status, current_version, submitted_at, updated_at)
SELECT cl.id, cg.id, 'NEEDS_REVISION', 1, NOW() - INTERVAL '9 days', NOW() - INTERVAL '8 days'
FROM course_lesson cl
         JOIN course c ON cl.course_id = c.id
         JOIN course_group cg ON cg.course_id = c.id
WHERE c.name = 'Сетевые технологии'
  AND cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND cg.title = 'Wi-Fi с аутентификацией 802.1X'
  AND NOT EXISTS (SELECT 1 FROM hearing_submission hs WHERE hs.lesson_id = cl.id AND hs.group_id = cg.id);

-- Группа 4: VPN-сервер — DRAFT
--changeset Diplom_Backend:2026-06-12-sub-8 splitStatements:true endDelimiter:;
INSERT INTO hearing_submission (lesson_id, group_id, status, current_version, submitted_at, updated_at)
SELECT cl.id, cg.id, 'DRAFT', 1, NOW() - INTERVAL '15 days', NOW() - INTERVAL '1 day'
FROM course_lesson cl
         JOIN course c ON cl.course_id = c.id
         JOIN course_group cg ON cg.course_id = c.id
WHERE c.name = 'Сетевые технологии'
  AND cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND cg.title = 'VPN-сервер для школы'
  AND NOT EXISTS (SELECT 1 FROM hearing_submission hs WHERE hs.lesson_id = cl.id AND hs.group_id = cg.id);

-- Группа 10: Балансировка нагрузки — DRAFT
--changeset Diplom_Backend:2026-06-12-sub-9 splitStatements:true endDelimiter:;
INSERT INTO hearing_submission (lesson_id, group_id, status, current_version, submitted_at, updated_at)
SELECT cl.id, cg.id, 'DRAFT', 1, NOW() - INTERVAL '20 days', NOW()
FROM course_lesson cl
         JOIN course c ON cl.course_id = c.id
         JOIN course_group cg ON cg.course_id = c.id
WHERE c.name = 'Сетевые технологии'
  AND cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND cg.title = 'Балансировка нагрузки в веб-сервисах'
  AND NOT EXISTS (SELECT 1 FROM hearing_submission hs WHERE hs.lesson_id = cl.id AND hs.group_id = cg.id);

-- Группа 6: Анализ трафика — REJECTED
--changeset Diplom_Backend:2026-06-12-sub-10 splitStatements:true endDelimiter:;
INSERT INTO hearing_submission (lesson_id, group_id, status, current_version, submitted_at, updated_at)
SELECT cl.id, cg.id, 'REJECTED', 1, NOW() - INTERVAL '12 days', NOW() - INTERVAL '10 days'
FROM course_lesson cl
         JOIN course c ON cl.course_id = c.id
         JOIN course_group cg ON cg.course_id = c.id
WHERE c.name = 'Сетевые технологии'
  AND cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND cg.title = 'Анализ трафика и сетевая безопасность'
  AND NOT EXISTS (SELECT 1 FROM hearing_submission hs WHERE hs.lesson_id = cl.id AND hs.group_id = cg.id);

-- =============================================================================
-- ЧАСТЬ 2: HEARING_REVIEW (используем существующих модераторов)
-- =============================================================================

-- Группа 1: ACCEPTED (модератор 1)
--changeset Diplom_Backend:2026-06-12-review-1 splitStatements:true endDelimiter:;
INSERT INTO hearing_review (submission_id, moderator_id, comment, grade, submission_version, reviewed_at)
SELECT hs.id,
       (SELECT id FROM account WHERE nickname IN ('ant.vasiliev', 'pol.kuptsova') LIMIT 1),
       'Отличная работа! Проект полностью соответствует требованиям. Презентация качественная, пояснительная записка оформлена по ГОСТ. Демонстрация прототипа показывает полную работоспособность.',
       42,
       hs.current_version,
       NOW() - INTERVAL '4 days'
FROM hearing_submission hs
         JOIN course_lesson cl ON hs.lesson_id = cl.id
         JOIN course_group cg ON hs.group_id = cg.id
WHERE cg.title = 'Чат-мессенджер в локальной сети'
  AND cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND NOT EXISTS (SELECT 1 FROM hearing_review hr WHERE hr.submission_id = hs.id);

-- Группа 7: ACCEPTED (модератор 2)
--changeset Diplom_Backend:2026-06-12-review-2 splitStatements:true endDelimiter:;
INSERT INTO hearing_review (submission_id, moderator_id, comment, grade, submission_version, reviewed_at)
SELECT hs.id,
       (SELECT id FROM account WHERE nickname = 'pol.kuptsova' LIMIT 1),
       'Превосходная работа! Глубокий анализ протоколов RIP и OSPF, качественная симуляция в GNS3, отличная презентация. Рекомендую для участия в конференции.',
       45,
       hs.current_version,
       NOW() - INTERVAL '2 days'
FROM hearing_submission hs
         JOIN course_lesson cl ON hs.lesson_id = cl.id
         JOIN course_group cg ON hs.group_id = cg.id
WHERE cg.title = 'Протоколы маршрутизации RIP и OSPF'
  AND cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND NOT EXISTS (SELECT 1 FROM hearing_review hr WHERE hr.submission_id = hs.id);

-- Группа 9: ACCEPTED (модератор 1)
--changeset Diplom_Backend:2026-06-12-review-3 splitStatements:true endDelimiter:;
INSERT INTO hearing_review (submission_id, moderator_id, comment, grade, submission_version, reviewed_at)
SELECT hs.id,
       (SELECT id FROM account WHERE nickname = 'ant.vasiliev' LIMIT 1),
       'Хорошая работа. Правила iptables настроены корректно, документация полная. Рекомендую усилить раздел с тестированием безопасности.',
       38,
       hs.current_version,
       NOW() - INTERVAL '5 days'
FROM hearing_submission hs
         JOIN course_lesson cl ON hs.lesson_id = cl.id
         JOIN course_group cg ON hs.group_id = cg.id
WHERE cg.title = 'Настройка межсетевого экрана'
  AND cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND NOT EXISTS (SELECT 1 FROM hearing_review hr WHERE hr.submission_id = hs.id);

-- Группа 3: NEEDS_REVISION (модератор 2)
--changeset Diplom_Backend:2026-06-12-review-4 splitStatements:true endDelimiter:;
INSERT INTO hearing_review (submission_id, moderator_id, comment, grade, submission_version, reviewed_at)
SELECT hs.id,
       (SELECT id FROM account WHERE nickname = 'pol.kuptsova' LIMIT 1),
       'Работа требует доработки по следующим пунктам:
1. Не хватает обоснования выбора сетевого оборудования
2. Отсутствует схема корпоративной сети
3. Пояснительная записка оформлена с нарушением требований
4. Презентация перегружена текстом

Необходимо исправить замечания и прислать на повторную проверку.',
       NULL,
       hs.current_version,
       NOW() - INTERVAL '6 days'
FROM hearing_submission hs
         JOIN course_lesson cl ON hs.lesson_id = cl.id
         JOIN course_group cg ON hs.group_id = cg.id
WHERE cg.title = 'Настройка корпоративной сети'
  AND cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND NOT EXISTS (SELECT 1 FROM hearing_review hr WHERE hr.submission_id = hs.id);

-- Группа 8: NEEDS_REVISION (модератор 1)
--changeset Diplom_Backend:2026-06-12-review-5 splitStatements:true endDelimiter:;
INSERT INTO hearing_review (submission_id, moderator_id, comment, grade, submission_version, reviewed_at)
SELECT hs.id,
       (SELECT id FROM account WHERE nickname = 'ant.vasiliev' LIMIT 1),
       'Работа требует доработки:
1. Добавить схему развёртывания RADIUS-сервера
2. Описать процесс настройки клиентских устройств
3. В презентации добавить скриншоты реальной настройки
4. Исправить орфографические ошибки в пояснительной записке',
       NULL,
       hs.current_version,
       NOW() - INTERVAL '7 days'
FROM hearing_submission hs
         JOIN course_lesson cl ON hs.lesson_id = cl.id
         JOIN course_group cg ON hs.group_id = cg.id
WHERE cg.title = 'Wi-Fi с аутентификацией 802.1X'
  AND cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND NOT EXISTS (SELECT 1 FROM hearing_review hr WHERE hr.submission_id = hs.id);

-- Группа 6: REJECTED (модератор 2)
--changeset Diplom_Backend:2026-06-12-review-6 splitStatements:true endDelimiter:;
INSERT INTO hearing_review (submission_id, moderator_id, comment, grade, submission_version, reviewed_at)
SELECT hs.id,
       (SELECT id FROM account WHERE nickname = 'pol.kuptsova' LIMIT 1),
       'Работа не может быть допущена к защите по следующим причинам:
1. Тема не раскрыта, отсутствует анализ трафика
2. Нет практической части и демонстрации
3. Пояснительная записка содержит менее 5 страниц
4. Презентация не соответствует требованиям

Рекомендовано повторное прохождение курса.',
       NULL,
       hs.current_version,
       NOW() - INTERVAL '9 days'
FROM hearing_submission hs
         JOIN course_lesson cl ON hs.lesson_id = cl.id
         JOIN course_group cg ON hs.group_id = cg.id
WHERE cg.title = 'Анализ трафика и сетевая безопасность'
  AND cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND NOT EXISTS (SELECT 1 FROM hearing_review hr WHERE hr.submission_id = hs.id);

-- =============================================================================
-- ЧАСТЬ 3: ОБНОВЛЕНИЕ ВЕРСИИ ДЛЯ ГРУППЫ 3 (повторная сдача)
-- =============================================================================

-- Обновляем статус и версию для группы 3 (Корпоративная сеть)
--changeset Diplom_Backend:2026-06-12-update-3 splitStatements:true endDelimiter:;
UPDATE hearing_submission
SET current_version = 2,
    status = 'ON_REVIEW',
    updated_at = NOW() - INTERVAL '2 days'
WHERE group_id IN (
    SELECT cg.id
    FROM course_group cg
    WHERE cg.title = 'Настройка корпоративной сети'
)
  AND lesson_id IN (
    SELECT cl.id
    FROM course_lesson cl
    WHERE cl.hearing_stage = 'CONFERENCE_DEFENSE'
)
  AND current_version = 1;
-- =============================================================================
-- ЧАСТЬ 4: SUBMISSION_REVIEW_HISTORY (история проверок индивидуальных работ)
-- =============================================================================

-- Иван Смирнов: урок 2 (ACCEPTED)
--changeset Diplom_Backend:2026-06-12-history-1 splitStatements:true endDelimiter:;
INSERT INTO submission_review_history (submission_id, reviewer_account_id, entry_kind, status_after, score, reviewer_comment, created_at)
SELECT ls.id,
       (SELECT id FROM account WHERE nickname = 'ant.vasiliev' LIMIT 1),
       'MODERATOR',
       'ACCEPTED',
       10,
       'Хороший анализ предметной области. Протоколы TCP/IP и UDP описаны верно.',
       NOW() - INTERVAL '53 days'
FROM lesson_submission ls
         JOIN course_lesson cl ON ls.lesson_id = cl.id
         JOIN account a ON ls.account_id = a.id
WHERE cl.order_number = 2
  AND a.nickname = 'ivan.smirnov'
  AND NOT EXISTS (SELECT 1 FROM submission_review_history srh WHERE srh.submission_id = ls.id);

-- Иван Смирнов: урок 5 (ACCEPTED)
--changeset Diplom_Backend:2026-06-12-history-2 splitStatements:true endDelimiter:;
INSERT INTO submission_review_history (submission_id, reviewer_account_id, entry_kind, status_after, score, reviewer_comment, created_at)
SELECT ls.id,
       (SELECT id FROM account WHERE nickname = 'pol.kuptsova' LIMIT 1),
       'MODERATOR',
       'ACCEPTED',
       15,
       'Прототип работает, базовый обмен сообщениями реализован. Отличный результат!',
       NOW() - INTERVAL '33 days'
FROM lesson_submission ls
         JOIN course_lesson cl ON ls.lesson_id = cl.id
         JOIN account a ON ls.account_id = a.id
WHERE cl.order_number = 5
  AND a.nickname = 'ivan.smirnov'
  AND NOT EXISTS (SELECT 1 FROM submission_review_history srh WHERE srh.submission_id = ls.id);

-- Мария Егорова: урок 4 (NEEDS_REVISION с ответом студента)
--changeset Diplom_Backend:2026-06-12-history-3 splitStatements:true endDelimiter:;
INSERT INTO submission_review_history (submission_id, reviewer_account_id, entry_kind, status_after, score, reviewer_comment, created_at)
SELECT ls.id,
       (SELECT id FROM account WHERE nickname = 'ant.vasiliev' LIMIT 1),
       'MODERATOR',
       'NEEDS_REVISION',
       NULL,
       'Схема сети нарисована хорошо, но не хватает описания конфигурации DHCP-пула. Добавьте, пожалуйста, какие адреса раздаются, какое время аренды и т.д.',
       NOW() - INTERVAL '28 days'
FROM lesson_submission ls
         JOIN course_lesson cl ON ls.lesson_id = cl.id
         JOIN account a ON ls.account_id = a.id
WHERE cl.order_number = 4
  AND a.nickname = 'mar.egor'
  AND NOT EXISTS (SELECT 1 FROM submission_review_history srh WHERE srh.submission_id = ls.id AND srh.entry_kind = 'MODERATOR');

-- Ответ студента на доработку
--changeset Diplom_Backend:2026-06-12-history-4 splitStatements:true endDelimiter:;
INSERT INTO submission_review_history (submission_id, reviewer_account_id, entry_kind, status_after, score, reviewer_comment, created_at)
SELECT ls.id,
       (SELECT id FROM account WHERE nickname = 'mar.egor' LIMIT 1),
       'STUDENT_REPLY',
       'NEEDS_REVISION',
       NULL,
       'Добавила описание DHCP-пула: диапазон 192.168.1.100-192.168.1.200, время аренды 24 часа, шлюз 192.168.1.1, DNS 8.8.8.8.',
       NOW() - INTERVAL '25 days'
FROM lesson_submission ls
         JOIN course_lesson cl ON ls.lesson_id = cl.id
         JOIN account a ON ls.account_id = a.id
WHERE cl.order_number = 4
  AND a.nickname = 'mar.egor'
  AND NOT EXISTS (SELECT 1 FROM submission_review_history srh WHERE srh.submission_id = ls.id AND srh.entry_kind = 'STUDENT_REPLY');