--liquibase formatted sql

-- =============================================================================
-- Данные по курсу «Сетевые технологии».
-- Группы, lesson_submission (уроки 2-5,7,8) и hearing_submission (1,6,9).
-- Не у всех учеников есть группа, не все сдали все этапы.
-- =============================================================================

-- ─────────────────────────────────────────────────────────────────────────────
-- ГРУППА 1: Смирнов Иван + Козлова Ольга
-- Тема: «Чат-мессенджер в локальной сети»
-- Статус: тема принята, уроки 2-5 сданы, промежуточный принят, уроки 7-8 сданы
-- ─────────────────────────────────────────────────────────────────────────────

--changeset Diplom_Backend:2026-06-08-g1-create splitStatements:true endDelimiter:;
INSERT INTO course_group (course_id, title, description, school_id, created_at)
SELECT c.id, 'Чат-мессенджер в локальной сети', 'Разработка защищённого мессенджера для локальной сети школы', a.school_id, NOW()
FROM course c, account a
WHERE c.name = 'Сетевые технологии' AND a.nickname = 'ivan.smirnov'
  AND NOT EXISTS (SELECT 1 FROM course_group cg WHERE cg.course_id = c.id AND cg.title = 'Чат-мессенджер в локальной сети');

--changeset Diplom_Backend:2026-06-08-g1-m1 splitStatements:true endDelimiter:;
INSERT INTO course_group_member (group_id, account_id, is_owner, joined_at)
SELECT cg.id, a.id, TRUE, NOW()
FROM course_group cg JOIN course c ON cg.course_id = c.id, account a
WHERE c.name = 'Сетевые технологии' AND cg.title = 'Чат-мессенджер в локальной сети' AND a.nickname = 'ivan.smirnov'
  AND NOT EXISTS (SELECT 1 FROM course_group_member m WHERE m.group_id = cg.id AND m.account_id = a.id);

--changeset Diplom_Backend:2026-06-08-g1-m2 splitStatements:true endDelimiter:;
INSERT INTO course_group_member (group_id, account_id, is_owner, joined_at)
SELECT cg.id, a.id, FALSE, NOW()
FROM course_group cg JOIN course c ON cg.course_id = c.id, account a
WHERE c.name = 'Сетевые технологии' AND cg.title = 'Чат-мессенджер в локальной сети' AND a.nickname = 'olga.kozlova'
  AND NOT EXISTS (SELECT 1 FROM course_group_member m WHERE m.group_id = cg.id AND m.account_id = a.id);

-- Слушание 1: TOPIC_APPROVAL — ACCEPTED
--changeset Diplom_Backend:2026-06-08-g1-h1 splitStatements:true endDelimiter:;
INSERT INTO hearing_submission (lesson_id, group_id, status, current_version, submitted_at, updated_at)
SELECT cl.id, cg.id, 'ACCEPTED', 1, NOW() - INTERVAL '60 days', NOW() - INTERVAL '55 days'
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
     JOIN course_group cg ON cg.course_id = c.id
WHERE c.name = 'Сетевые технологии' AND cl.order_number = 1 AND cg.title = 'Чат-мессенджер в локальной сети'
  AND NOT EXISTS (SELECT 1 FROM hearing_submission hs WHERE hs.lesson_id = cl.id AND hs.group_id = cg.id);

-- Уроки 2-5: ACCEPTED
--changeset Diplom_Backend:2026-06-08-g1-l2-ivan splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Провёл анализ предметной области: протоколы TCP/IP, UDP, требования к мессенджеру.', 'ACCEPTED', 10, NOW() - INTERVAL '55 days', NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id, account a
WHERE c.name = 'Сетевые технологии' AND cl.order_number = 2 AND a.nickname = 'ivan.smirnov'
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-08-g1-l3-ivan splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Проанализировал аналоги: Telegram, Slack, Discord. Наш мессенджер работает в локальной сети без интернета.', 'ACCEPTED', 10, NOW() - INTERVAL '50 days', NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id, account a
WHERE c.name = 'Сетевые технологии' AND cl.order_number = 3 AND a.nickname = 'ivan.smirnov'
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-08-g1-l4-ivan splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Спроектировал клиент-серверную архитектуру: Python + сокеты + SQLite для хранения сообщений.', 'ACCEPTED', 10, NOW() - INTERVAL '45 days', NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id, account a
WHERE c.name = 'Сетевые технологии' AND cl.order_number = 4 AND a.nickname = 'ivan.smirnov'
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-08-g1-l5-ivan splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Прототип мессенджера готов: базовый обмен сообщениями работает.', 'ACCEPTED', 15, NOW() - INTERVAL '35 days', NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id, account a
WHERE c.name = 'Сетевые технологии' AND cl.order_number = 5 AND a.nickname = 'ivan.smirnov'
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

-- Слушание 6: INTERMEDIATE — ACCEPTED
--changeset Diplom_Backend:2026-06-08-g1-h6 splitStatements:true endDelimiter:;
INSERT INTO hearing_submission (lesson_id, group_id, status, current_version, submitted_at, updated_at)
SELECT cl.id, cg.id, 'ACCEPTED', 1, NOW() - INTERVAL '30 days', NOW() - INTERVAL '25 days'
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
     JOIN course_group cg ON cg.course_id = c.id
WHERE c.name = 'Сетевые технологии' AND cl.order_number = 6 AND cg.title = 'Чат-мессенджер в локальной сети'
  AND NOT EXISTS (SELECT 1 FROM hearing_submission hs WHERE hs.lesson_id = cl.id AND hs.group_id = cg.id);

--changeset Diplom_Backend:2026-06-08-g1-l7-ivan splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Пояснительная записка оформлена по шаблону МосПолитех.', 'ACCEPTED', 10, NOW() - INTERVAL '20 days', NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id, account a
WHERE c.name = 'Сетевые технологии' AND cl.order_number = 7 AND a.nickname = 'ivan.smirnov'
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-08-g1-l8-ivan splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Презентация на 10 слайдов: цель, архитектура, демонстрация, результаты.', 'ACCEPTED', 15, NOW() - INTERVAL '15 days', NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id, account a
WHERE c.name = 'Сетевые технологии' AND cl.order_number = 8 AND a.nickname = 'ivan.smirnov'
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

-- ─────────────────────────────────────────────────────────────────────────────
-- ГРУППА 2: Николаев Дмитрий + Соколова Екатерина
-- Тема: «Система мониторинга сети»
-- Статус: тема принята, уроки 2-4 сданы, урок 5 на проверке, промежуточный нет
-- ─────────────────────────────────────────────────────────────────────────────

--changeset Diplom_Backend:2026-06-08-g2-create splitStatements:true endDelimiter:;
INSERT INTO course_group (course_id, title, description, school_id, created_at)
SELECT c.id, 'Система мониторинга сети', 'Мониторинг доступности серверов и сервисов школьной сети', a.school_id, NOW()
FROM course c, account a
WHERE c.name = 'Сетевые технологии' AND a.nickname = 'dmitry.n'
  AND NOT EXISTS (SELECT 1 FROM course_group cg WHERE cg.course_id = c.id AND cg.title = 'Система мониторинга сети');

--changeset Diplom_Backend:2026-06-08-g2-m1 splitStatements:true endDelimiter:;
INSERT INTO course_group_member (group_id, account_id, is_owner, joined_at)
SELECT cg.id, a.id, TRUE, NOW()
FROM course_group cg JOIN course c ON cg.course_id = c.id, account a
WHERE c.name = 'Сетевые технологии' AND cg.title = 'Система мониторинга сети' AND a.nickname = 'dmitry.n'
  AND NOT EXISTS (SELECT 1 FROM course_group_member m WHERE m.group_id = cg.id AND m.account_id = a.id);

--changeset Diplom_Backend:2026-06-08-g2-m2 splitStatements:true endDelimiter:;
INSERT INTO course_group_member (group_id, account_id, is_owner, joined_at)
SELECT cg.id, a.id, FALSE, NOW()
FROM course_group cg JOIN course c ON cg.course_id = c.id, account a
WHERE c.name = 'Сетевые технологии' AND cg.title = 'Система мониторинга сети' AND a.nickname = 'kat.sokolova'
  AND NOT EXISTS (SELECT 1 FROM course_group_member m WHERE m.group_id = cg.id AND m.account_id = a.id);

-- Слушание 1: ACCEPTED
--changeset Diplom_Backend:2026-06-08-g2-h1 splitStatements:true endDelimiter:;
INSERT INTO hearing_submission (lesson_id, group_id, status, current_version, submitted_at, updated_at)
SELECT cl.id, cg.id, 'ACCEPTED', 1, NOW() - INTERVAL '58 days', NOW() - INTERVAL '53 days'
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
     JOIN course_group cg ON cg.course_id = c.id
WHERE c.name = 'Сетевые технологии' AND cl.order_number = 1 AND cg.title = 'Система мониторинга сети'
  AND NOT EXISTS (SELECT 1 FROM hearing_submission hs WHERE hs.lesson_id = cl.id AND hs.group_id = cg.id);

--changeset Diplom_Backend:2026-06-08-g2-l2 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Анализ предметной области: протоколы ICMP, SNMP, HTTP для мониторинга.', 'ACCEPTED', 10, NOW() - INTERVAL '50 days', NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id, account a
WHERE c.name = 'Сетевые технологии' AND cl.order_number = 2 AND a.nickname = 'dmitry.n'
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-08-g2-l3 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Аналоги: Zabbix, Nagios, Prometheus. Наш инструмент легковесный и не требует сервера.', 'ACCEPTED', 10, NOW() - INTERVAL '45 days', NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id, account a
WHERE c.name = 'Сетевые технологии' AND cl.order_number = 3 AND a.nickname = 'dmitry.n'
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-08-g2-l4 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Спроектировал архитектуру: Python-агент на каждом узле + центральный дашборд.', 'ACCEPTED', 10, NOW() - INTERVAL '40 days', NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id, account a
WHERE c.name = 'Сетевые технологии' AND cl.order_number = 4 AND a.nickname = 'dmitry.n'
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-08-g2-l5 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Прототип системы мониторинга готов, дашборд отображает статусы.', 'SUBMITTED', NULL, NOW() - INTERVAL '10 days', NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id, account a
WHERE c.name = 'Сетевые технологии' AND cl.order_number = 5 AND a.nickname = 'dmitry.n'
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

-- ─────────────────────────────────────────────────────────────────────────────
-- ГРУППА 3: Петров Алексей + Михайлова Анна
-- Тема: «Настройка корпоративной сети»
-- Статус: тема на проверке (NEEDS_REVISION), уроки 2-3 сданы
-- ─────────────────────────────────────────────────────────────────────────────

--changeset Diplom_Backend:2026-06-08-g3-create splitStatements:true endDelimiter:;
INSERT INTO course_group (course_id, title, description, school_id, created_at)
SELECT c.id, 'Настройка корпоративной сети', 'Проектирование и настройка корпоративной сети для офиса', a.school_id, NOW()
FROM course c, account a
WHERE c.name = 'Сетевые технологии' AND a.nickname = 'alex.petrov'
  AND NOT EXISTS (SELECT 1 FROM course_group cg WHERE cg.course_id = c.id AND cg.title = 'Настройка корпоративной сети');

--changeset Diplom_Backend:2026-06-08-g3-m1 splitStatements:true endDelimiter:;
INSERT INTO course_group_member (group_id, account_id, is_owner, joined_at)
SELECT cg.id, a.id, TRUE, NOW()
FROM course_group cg JOIN course c ON cg.course_id = c.id, account a
WHERE c.name = 'Сетевые технологии' AND cg.title = 'Настройка корпоративной сети' AND a.nickname = 'alex.petrov'
  AND NOT EXISTS (SELECT 1 FROM course_group_member m WHERE m.group_id = cg.id AND m.account_id = a.id);

--changeset Diplom_Backend:2026-06-08-g3-m2 splitStatements:true endDelimiter:;
INSERT INTO course_group_member (group_id, account_id, is_owner, joined_at)
SELECT cg.id, a.id, FALSE, NOW()
FROM course_group cg JOIN course c ON cg.course_id = c.id, account a
WHERE c.name = 'Сетевые технологии' AND cg.title = 'Настройка корпоративной сети' AND a.nickname = 'ann.mikh'
  AND NOT EXISTS (SELECT 1 FROM course_group_member m WHERE m.group_id = cg.id AND m.account_id = a.id);

-- Слушание 1: NEEDS_REVISION
--changeset Diplom_Backend:2026-06-08-g3-h1 splitStatements:true endDelimiter:;
INSERT INTO hearing_submission (lesson_id, group_id, status, current_version, submitted_at, updated_at)
SELECT cl.id, cg.id, 'NEEDS_REVISION', 1, NOW() - INTERVAL '55 days', NOW() - INTERVAL '50 days'
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
     JOIN course_group cg ON cg.course_id = c.id
WHERE c.name = 'Сетевые технологии' AND cl.order_number = 1 AND cg.title = 'Настройка корпоративной сети'
  AND NOT EXISTS (SELECT 1 FROM hearing_submission hs WHERE hs.lesson_id = cl.id AND hs.group_id = cg.id);

--changeset Diplom_Backend:2026-06-08-g3-l2 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Провёл анализ: протоколы VLAN, DHCP, DNS в корпоративных сетях.', 'ACCEPTED', 10, NOW() - INTERVAL '48 days', NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id, account a
WHERE c.name = 'Сетевые технологии' AND cl.order_number = 2 AND a.nickname = 'alex.petrov'
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-08-g3-l3 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Аналоги: Cisco Packet Tracer сети, GNS3. Наш проект — реальная настройка Raspberry Pi.', 'SUBMITTED', NULL, NOW() - INTERVAL '20 days', NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id, account a
WHERE c.name = 'Сетевые технологии' AND cl.order_number = 3 AND a.nickname = 'alex.petrov'
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

-- ─────────────────────────────────────────────────────────────────────────────
-- ГРУППА 4: Фёдоров Сергей + Васильева Елена + Павлов Андрей
-- Тема: «VPN-сервер для школы»
-- Статус: тема принята, промежуточный на проверке, уроки 2-5 приняты, 7 сдан
-- ─────────────────────────────────────────────────────────────────────────────

--changeset Diplom_Backend:2026-06-08-g4-create splitStatements:true endDelimiter:;
INSERT INTO course_group (course_id, title, description, school_id, created_at)
SELECT c.id, 'VPN-сервер для школы', 'Развёртывание и настройка VPN-сервера для безопасного доступа', a.school_id, NOW()
FROM course c, account a
WHERE c.name = 'Сетевые технологии' AND a.nickname = 's.fedorov'
  AND NOT EXISTS (SELECT 1 FROM course_group cg WHERE cg.course_id = c.id AND cg.title = 'VPN-сервер для школы');

--changeset Diplom_Backend:2026-06-08-g4-m1 splitStatements:true endDelimiter:;
INSERT INTO course_group_member (group_id, account_id, is_owner, joined_at)
SELECT cg.id, a.id, TRUE, NOW()
FROM course_group cg JOIN course c ON cg.course_id = c.id, account a
WHERE c.name = 'Сетевые технологии' AND cg.title = 'VPN-сервер для школы' AND a.nickname = 's.fedorov'
  AND NOT EXISTS (SELECT 1 FROM course_group_member m WHERE m.group_id = cg.id AND m.account_id = a.id);

--changeset Diplom_Backend:2026-06-08-g4-m2 splitStatements:true endDelimiter:;
INSERT INTO course_group_member (group_id, account_id, is_owner, joined_at)
SELECT cg.id, a.id, FALSE, NOW()
FROM course_group cg JOIN course c ON cg.course_id = c.id, account a
WHERE c.name = 'Сетевые технологии' AND cg.title = 'VPN-сервер для школы' AND a.nickname = 'el.vasil'
  AND NOT EXISTS (SELECT 1 FROM course_group_member m WHERE m.group_id = cg.id AND m.account_id = a.id);

--changeset Diplom_Backend:2026-06-08-g4-m3 splitStatements:true endDelimiter:;
INSERT INTO course_group_member (group_id, account_id, is_owner, joined_at)
SELECT cg.id, a.id, FALSE, NOW()
FROM course_group cg JOIN course c ON cg.course_id = c.id, account a
WHERE c.name = 'Сетевые технологии' AND cg.title = 'VPN-сервер для школы' AND a.nickname = 'and.pavlov'
  AND NOT EXISTS (SELECT 1 FROM course_group_member m WHERE m.group_id = cg.id AND m.account_id = a.id);

-- Слушание 1: ACCEPTED
--changeset Diplom_Backend:2026-06-08-g4-h1 splitStatements:true endDelimiter:;
INSERT INTO hearing_submission (lesson_id, group_id, status, current_version, submitted_at, updated_at)
SELECT cl.id, cg.id, 'ACCEPTED', 1, NOW() - INTERVAL '62 days', NOW() - INTERVAL '58 days'
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
     JOIN course_group cg ON cg.course_id = c.id
WHERE c.name = 'Сетевые технологии' AND cl.order_number = 1 AND cg.title = 'VPN-сервер для школы'
  AND NOT EXISTS (SELECT 1 FROM hearing_submission hs WHERE hs.lesson_id = cl.id AND hs.group_id = cg.id);

--changeset Diplom_Backend:2026-06-08-g4-l2 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Анализ предметной области VPN: протоколы OpenVPN, WireGuard, IPSec.', 'ACCEPTED', 10, NOW() - INTERVAL '55 days', NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id, account a
WHERE c.name = 'Сетевые технологии' AND cl.order_number = 2 AND a.nickname = 's.fedorov'
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-08-g4-l3 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Аналоги: NordVPN, ProtonVPN. Наш сервер на WireGuard — быстрее и проще в настройке.', 'ACCEPTED', 10, NOW() - INTERVAL '50 days', NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id, account a
WHERE c.name = 'Сетевые технологии' AND cl.order_number = 3 AND a.nickname = 's.fedorov'
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-08-g4-l4 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Спроектировал сетевую топологию: сервер на Ubuntu, клиенты Windows и Android.', 'ACCEPTED', 10, NOW() - INTERVAL '43 days', NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id, account a
WHERE c.name = 'Сетевые технологии' AND cl.order_number = 4 AND a.nickname = 's.fedorov'
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-08-g4-l5 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'WireGuard-сервер развёрнут, подключение работает с трёх устройств.', 'ACCEPTED', 15, NOW() - INTERVAL '35 days', NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id, account a
WHERE c.name = 'Сетевые технологии' AND cl.order_number = 5 AND a.nickname = 's.fedorov'
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

-- Слушание 6: ON_REVIEW (промежуточный на проверке)
--changeset Diplom_Backend:2026-06-08-g4-h6 splitStatements:true endDelimiter:;
INSERT INTO hearing_submission (lesson_id, group_id, status, current_version, submitted_at, updated_at)
SELECT cl.id, cg.id, 'ON_REVIEW', 1, NOW() - INTERVAL '5 days', NOW() - INTERVAL '5 days'
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
     JOIN course_group cg ON cg.course_id = c.id
WHERE c.name = 'Сетевые технологии' AND cl.order_number = 6 AND cg.title = 'VPN-сервер для школы'
  AND NOT EXISTS (SELECT 1 FROM hearing_submission hs WHERE hs.lesson_id = cl.id AND hs.group_id = cg.id);

--changeset Diplom_Backend:2026-06-08-g4-l7 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Пояснительная записка оформлена: введение, архитектура, инструкция по развёртыванию.', 'SUBMITTED', NULL, NOW() - INTERVAL '3 days', NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id, account a
WHERE c.name = 'Сетевые технологии' AND cl.order_number = 7 AND a.nickname = 's.fedorov'
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

-- ─────────────────────────────────────────────────────────────────────────────
-- ГРУППА 5: Егорова Мария (одна)
-- Тема: «Настройка DNS и DHCP-сервера»
-- Статус: тема принята, уроки 2-3 приняты, урок 4 на доработку
-- ─────────────────────────────────────────────────────────────────────────────

--changeset Diplom_Backend:2026-06-08-g5-create splitStatements:true endDelimiter:;
INSERT INTO course_group (course_id, title, description, school_id, created_at)
SELECT c.id, 'Настройка DNS и DHCP-сервера', 'Развёртывание DNS и DHCP для школьной сети', a.school_id, NOW()
FROM course c, account a
WHERE c.name = 'Сетевые технологии' AND a.nickname = 'mar.egor'
  AND NOT EXISTS (SELECT 1 FROM course_group cg WHERE cg.course_id = c.id AND cg.title = 'Настройка DNS и DHCP-сервера');

--changeset Diplom_Backend:2026-06-08-g5-m1 splitStatements:true endDelimiter:;
INSERT INTO course_group_member (group_id, account_id, is_owner, joined_at)
SELECT cg.id, a.id, TRUE, NOW()
FROM course_group cg JOIN course c ON cg.course_id = c.id, account a
WHERE c.name = 'Сетевые технологии' AND cg.title = 'Настройка DNS и DHCP-сервера' AND a.nickname = 'mar.egor'
  AND NOT EXISTS (SELECT 1 FROM course_group_member m WHERE m.group_id = cg.id AND m.account_id = a.id);

-- Слушание 1: ACCEPTED
--changeset Diplom_Backend:2026-06-08-g5-h1 splitStatements:true endDelimiter:;
INSERT INTO hearing_submission (lesson_id, group_id, status, current_version, submitted_at, updated_at)
SELECT cl.id, cg.id, 'ACCEPTED', 1, NOW() - INTERVAL '57 days', NOW() - INTERVAL '52 days'
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
     JOIN course_group cg ON cg.course_id = c.id
WHERE c.name = 'Сетевые технологии' AND cl.order_number = 1 AND cg.title = 'Настройка DNS и DHCP-сервера'
  AND NOT EXISTS (SELECT 1 FROM hearing_submission hs WHERE hs.lesson_id = cl.id AND hs.group_id = cg.id);

--changeset Diplom_Backend:2026-06-08-g5-l2 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Изучила протоколы DNS (RFC 1035) и DHCP (RFC 2131). Провела анализ ролей в сети.', 'ACCEPTED', 10, NOW() - INTERVAL '50 days', NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id, account a
WHERE c.name = 'Сетевые технологии' AND cl.order_number = 2 AND a.nickname = 'mar.egor'
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-08-g5-l3 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Аналоги: bind9, dnsmasq, Windows DHCP Server. Выбрала dnsmasq как самый лёгкий.', 'ACCEPTED', 10, NOW() - INTERVAL '45 days', NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id, account a
WHERE c.name = 'Сетевые технологии' AND cl.order_number = 3 AND a.nickname = 'mar.egor'
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-08-g5-l4 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Схема сети нарисована, но не хватает описания конфигурации DHCP-пула.', 'NEEDS_REVISION', NULL, NOW() - INTERVAL '30 days', NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id, account a
WHERE c.name = 'Сетевые технологии' AND cl.order_number = 4 AND a.nickname = 'mar.egor'
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

-- ─────────────────────────────────────────────────────────────────────────────
-- ГРУППА 6: Семёнов Николай + Иванова Татьяна
-- Тема: «Анализ трафика и сетевая безопасность»
-- Статус: тема SUBMITTED (ещё на проверке), уроки не сдавали
-- ─────────────────────────────────────────────────────────────────────────────

--changeset Diplom_Backend:2026-06-08-g6-create splitStatements:true endDelimiter:;
INSERT INTO course_group (course_id, title, description, school_id, created_at)
SELECT c.id, 'Анализ трафика и сетевая безопасность', 'Использование Wireshark для анализа и защиты сети', a.school_id, NOW()
FROM course c, account a
WHERE c.name = 'Сетевые технологии' AND a.nickname = 'nik.semen'
  AND NOT EXISTS (SELECT 1 FROM course_group cg WHERE cg.course_id = c.id AND cg.title = 'Анализ трафика и сетевая безопасность');

--changeset Diplom_Backend:2026-06-08-g6-m1 splitStatements:true endDelimiter:;
INSERT INTO course_group_member (group_id, account_id, is_owner, joined_at)
SELECT cg.id, a.id, TRUE, NOW()
FROM course_group cg JOIN course c ON cg.course_id = c.id, account a
WHERE c.name = 'Сетевые технологии' AND cg.title = 'Анализ трафика и сетевая безопасность' AND a.nickname = 'nik.semen'
  AND NOT EXISTS (SELECT 1 FROM course_group_member m WHERE m.group_id = cg.id AND m.account_id = a.id);

--changeset Diplom_Backend:2026-06-08-g6-m2 splitStatements:true endDelimiter:;
INSERT INTO course_group_member (group_id, account_id, is_owner, joined_at)
SELECT cg.id, a.id, FALSE, NOW()
FROM course_group cg JOIN course c ON cg.course_id = c.id, account a
WHERE c.name = 'Сетевые технологии' AND cg.title = 'Анализ трафика и сетевая безопасность' AND a.nickname = 'tat.ivan'
  AND NOT EXISTS (SELECT 1 FROM course_group_member m WHERE m.group_id = cg.id AND m.account_id = a.id);

-- Слушание 1: ON_REVIEW
--changeset Diplom_Backend:2026-06-08-g6-h1 splitStatements:true endDelimiter:;
INSERT INTO hearing_submission (lesson_id, group_id, status, current_version, submitted_at, updated_at)
SELECT cl.id, cg.id, 'ON_REVIEW', 1, NOW() - INTERVAL '3 days', NOW() - INTERVAL '3 days'
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
     JOIN course_group cg ON cg.course_id = c.id
WHERE c.name = 'Сетевые технологии' AND cl.order_number = 1 AND cg.title = 'Анализ трафика и сетевая безопасность'
  AND NOT EXISTS (SELECT 1 FROM hearing_submission hs WHERE hs.lesson_id = cl.id AND hs.group_id = cg.id);

-- ─────────────────────────────────────────────────────────────────────────────
-- ГРУППА 7: Морозов Владимир + Новикова Надежда
-- Тема: «Протоколы маршрутизации RIP и OSPF»
-- Статус: тема принята, уроки 2-5 приняты, промежуточный принят, урок 7 принят
-- (самые продвинутые — у финальной защиты)
-- ─────────────────────────────────────────────────────────────────────────────

--changeset Diplom_Backend:2026-06-08-g7-create splitStatements:true endDelimiter:;
INSERT INTO course_group (course_id, title, description, school_id, created_at)
SELECT c.id, 'Протоколы маршрутизации RIP и OSPF', 'Сравнение и настройка протоколов динамической маршрутизации', a.school_id, NOW()
FROM course c, account a
WHERE c.name = 'Сетевые технологии' AND a.nickname = 'vlad.mor'
  AND NOT EXISTS (SELECT 1 FROM course_group cg WHERE cg.course_id = c.id AND cg.title = 'Протоколы маршрутизации RIP и OSPF');

--changeset Diplom_Backend:2026-06-08-g7-m1 splitStatements:true endDelimiter:;
INSERT INTO course_group_member (group_id, account_id, is_owner, joined_at)
SELECT cg.id, a.id, TRUE, NOW()
FROM course_group cg JOIN course c ON cg.course_id = c.id, account a
WHERE c.name = 'Сетевые технологии' AND cg.title = 'Протоколы маршрутизации RIP и OSPF' AND a.nickname = 'vlad.mor'
  AND NOT EXISTS (SELECT 1 FROM course_group_member m WHERE m.group_id = cg.id AND m.account_id = a.id);

--changeset Diplom_Backend:2026-06-08-g7-m2 splitStatements:true endDelimiter:;
INSERT INTO course_group_member (group_id, account_id, is_owner, joined_at)
SELECT cg.id, a.id, FALSE, NOW()
FROM course_group cg JOIN course c ON cg.course_id = c.id, account a
WHERE c.name = 'Сетевые технологии' AND cg.title = 'Протоколы маршрутизации RIP и OSPF' AND a.nickname = 'nad.nov'
  AND NOT EXISTS (SELECT 1 FROM course_group_member m WHERE m.group_id = cg.id AND m.account_id = a.id);

-- Слушание 1: ACCEPTED
--changeset Diplom_Backend:2026-06-08-g7-h1 splitStatements:true endDelimiter:;
INSERT INTO hearing_submission (lesson_id, group_id, status, current_version, submitted_at, updated_at)
SELECT cl.id, cg.id, 'ACCEPTED', 1, NOW() - INTERVAL '65 days', NOW() - INTERVAL '60 days'
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
     JOIN course_group cg ON cg.course_id = c.id
WHERE c.name = 'Сетевые технологии' AND cl.order_number = 1 AND cg.title = 'Протоколы маршрутизации RIP и OSPF'
  AND NOT EXISTS (SELECT 1 FROM hearing_submission hs WHERE hs.lesson_id = cl.id AND hs.group_id = cg.id);

--changeset Diplom_Backend:2026-06-08-g7-l2 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Изучил RIP (дистанционно-векторный) и OSPF (состояния канала). Ключевые отличия — скорость сходимости.', 'ACCEPTED', 10, NOW() - INTERVAL '58 days', NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id, account a
WHERE c.name = 'Сетевые технологии' AND cl.order_number = 2 AND a.nickname = 'vlad.mor'
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-08-g7-l3 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Аналоги: Cisco IOS, MikroTik RouterOS. Мы используем GNS3 для симуляции топологии.', 'ACCEPTED', 10, NOW() - INTERVAL '52 days', NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id, account a
WHERE c.name = 'Сетевые технологии' AND cl.order_number = 3 AND a.nickname = 'vlad.mor'
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-08-g7-l4 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Спроектирована топология из 4 маршрутизаторов. Настроены RIP v2 и OSPF зона 0.', 'ACCEPTED', 10, NOW() - INTERVAL '45 days', NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id, account a
WHERE c.name = 'Сетевые технологии' AND cl.order_number = 4 AND a.nickname = 'vlad.mor'
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-08-g7-l5 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Прототип сети запущен в GNS3: OSPF сходится быстрее RIP при отказе узла.', 'ACCEPTED', 15, NOW() - INTERVAL '38 days', NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id, account a
WHERE c.name = 'Сетевые технологии' AND cl.order_number = 5 AND a.nickname = 'vlad.mor'
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

-- Слушание 6: ACCEPTED
--changeset Diplom_Backend:2026-06-08-g7-h6 splitStatements:true endDelimiter:;
INSERT INTO hearing_submission (lesson_id, group_id, status, current_version, submitted_at, updated_at)
SELECT cl.id, cg.id, 'ACCEPTED', 1, NOW() - INTERVAL '28 days', NOW() - INTERVAL '22 days'
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
     JOIN course_group cg ON cg.course_id = c.id
WHERE c.name = 'Сетевые технологии' AND cl.order_number = 6 AND cg.title = 'Протоколы маршрутизации RIP и OSPF'
  AND NOT EXISTS (SELECT 1 FROM hearing_submission hs WHERE hs.lesson_id = cl.id AND hs.group_id = cg.id);

--changeset Diplom_Backend:2026-06-08-g7-l7 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Пояснительная записка написана: введение, теория, практическая часть, заключение.', 'ACCEPTED', 10, NOW() - INTERVAL '18 days', NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id, account a
WHERE c.name = 'Сетевые технологии' AND cl.order_number = 7 AND a.nickname = 'vlad.mor'
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-08-g7-l8 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Презентация готова: 12 слайдов, демонстрация симуляции в GNS3.', 'SUBMITTED', NULL, NOW() - INTERVAL '7 days', NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id, account a
WHERE c.name = 'Сетевые технологии' AND cl.order_number = 8 AND a.nickname = 'vlad.mor'
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

-- ─────────────────────────────────────────────────────────────────────────────
-- ГРУППА 8: Зайцева Ирина + Кузнецов Михаил
-- Тема: «Настройка Wi-Fi сети с аутентификацией 802.1X»
-- Статус: тема принята, только урок 2 сдан
-- ─────────────────────────────────────────────────────────────────────────────

--changeset Diplom_Backend:2026-06-08-g8-create splitStatements:true endDelimiter:;
INSERT INTO course_group (course_id, title, description, school_id, created_at)
SELECT c.id, 'Wi-Fi с аутентификацией 802.1X', 'Защищённая Wi-Fi сеть с RADIUS-сервером', a.school_id, NOW()
FROM course c, account a
WHERE c.name = 'Сетевые технологии' AND a.nickname = 'ir.zayt'
  AND NOT EXISTS (SELECT 1 FROM course_group cg WHERE cg.course_id = c.id AND cg.title = 'Wi-Fi с аутентификацией 802.1X');

--changeset Diplom_Backend:2026-06-08-g8-m1 splitStatements:true endDelimiter:;
INSERT INTO course_group_member (group_id, account_id, is_owner, joined_at)
SELECT cg.id, a.id, TRUE, NOW()
FROM course_group cg JOIN course c ON cg.course_id = c.id, account a
WHERE c.name = 'Сетевые технологии' AND cg.title = 'Wi-Fi с аутентификацией 802.1X' AND a.nickname = 'ir.zayt'
  AND NOT EXISTS (SELECT 1 FROM course_group_member m WHERE m.group_id = cg.id AND m.account_id = a.id);

--changeset Diplom_Backend:2026-06-08-g8-m2 splitStatements:true endDelimiter:;
INSERT INTO course_group_member (group_id, account_id, is_owner, joined_at)
SELECT cg.id, a.id, FALSE, NOW()
FROM course_group cg JOIN course c ON cg.course_id = c.id, account a
WHERE c.name = 'Сетевые технологии' AND cg.title = 'Wi-Fi с аутентификацией 802.1X' AND a.nickname = 'mikhail.kuz'
  AND NOT EXISTS (SELECT 1 FROM course_group_member m WHERE m.group_id = cg.id AND m.account_id = a.id);

-- Слушание 1: ACCEPTED
--changeset Diplom_Backend:2026-06-08-g8-h1 splitStatements:true endDelimiter:;
INSERT INTO hearing_submission (lesson_id, group_id, status, current_version, submitted_at, updated_at)
SELECT cl.id, cg.id, 'ACCEPTED', 1, NOW() - INTERVAL '53 days', NOW() - INTERVAL '48 days'
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
     JOIN course_group cg ON cg.course_id = c.id
WHERE c.name = 'Сетевые технологии' AND cl.order_number = 1 AND cg.title = 'Wi-Fi с аутентификацией 802.1X'
  AND NOT EXISTS (SELECT 1 FROM hearing_submission hs WHERE hs.lesson_id = cl.id AND hs.group_id = cg.id);

--changeset Diplom_Backend:2026-06-08-g8-l2 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Изучила стандарт IEEE 802.1X, протоколы EAP и RADIUS. Провела анализ требований к безопасности.', 'ACCEPTED', 10, NOW() - INTERVAL '45 days', NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id, account a
WHERE c.name = 'Сетевые технологии' AND cl.order_number = 2 AND a.nickname = 'ir.zayt'
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

-- ─────────────────────────────────────────────────────────────────────────────
-- ГРУППА 9: Попова Виктория (одна)
-- Тема: «Настройка межсетевого экрана»
-- Статус: тема принята, уроки 2-4 приняты
-- ─────────────────────────────────────────────────────────────────────────────

--changeset Diplom_Backend:2026-06-08-g9-create splitStatements:true endDelimiter:;
INSERT INTO course_group (course_id, title, description, school_id, created_at)
SELECT c.id, 'Настройка межсетевого экрана', 'Конфигурация iptables для защиты школьного сервера', a.school_id, NOW()
FROM course c, account a
WHERE c.name = 'Сетевые технологии' AND a.nickname = 'vika.pop'
  AND NOT EXISTS (SELECT 1 FROM course_group cg WHERE cg.course_id = c.id AND cg.title = 'Настройка межсетевого экрана');

--changeset Diplom_Backend:2026-06-08-g9-m1 splitStatements:true endDelimiter:;
INSERT INTO course_group_member (group_id, account_id, is_owner, joined_at)
SELECT cg.id, a.id, TRUE, NOW()
FROM course_group cg JOIN course c ON cg.course_id = c.id, account a
WHERE c.name = 'Сетевые технологии' AND cg.title = 'Настройка межсетевого экрана' AND a.nickname = 'vika.pop'
  AND NOT EXISTS (SELECT 1 FROM course_group_member m WHERE m.group_id = cg.id AND m.account_id = a.id);

-- Слушание 1: ACCEPTED
--changeset Diplom_Backend:2026-06-08-g9-h1 splitStatements:true endDelimiter:;
INSERT INTO hearing_submission (lesson_id, group_id, status, current_version, submitted_at, updated_at)
SELECT cl.id, cg.id, 'ACCEPTED', 1, NOW() - INTERVAL '56 days', NOW() - INTERVAL '51 days'
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
     JOIN course_group cg ON cg.course_id = c.id
WHERE c.name = 'Сетевые технологии' AND cl.order_number = 1 AND cg.title = 'Настройка межсетевого экрана'
  AND NOT EXISTS (SELECT 1 FROM hearing_submission hs WHERE hs.lesson_id = cl.id AND hs.group_id = cg.id);

--changeset Diplom_Backend:2026-06-08-g9-l2 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Изучила принципы фильтрации пакетов, stateful inspection и NAT.', 'ACCEPTED', 10, NOW() - INTERVAL '49 days', NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id, account a
WHERE c.name = 'Сетевые технологии' AND cl.order_number = 2 AND a.nickname = 'vika.pop'
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-08-g9-l3 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Аналоги: pfSense, UFW, Windows Firewall. Выбрала iptables для гибкости настройки.', 'ACCEPTED', 10, NOW() - INTERVAL '43 days', NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id, account a
WHERE c.name = 'Сетевые технологии' AND cl.order_number = 3 AND a.nickname = 'vika.pop'
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-08-g9-l4 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Спроектировала правила iptables: блокировка входящего трафика, разрешение SSH и HTTP.', 'ACCEPTED', 10, NOW() - INTERVAL '35 days', NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id, account a
WHERE c.name = 'Сетевые технологии' AND cl.order_number = 4 AND a.nickname = 'vika.pop'
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

-- ─────────────────────────────────────────────────────────────────────────────
-- ГРУППА 10: Романов Александр + Григорьева Людмила
-- Тема: «Балансировка нагрузки в веб-сервисах»
-- Статус: тема на проверке (ON_REVIEW), уроки не сданы
-- ─────────────────────────────────────────────────────────────────────────────

--changeset Diplom_Backend:2026-06-08-g10-create splitStatements:true endDelimiter:;
INSERT INTO course_group (course_id, title, description, school_id, created_at)
SELECT c.id, 'Балансировка нагрузки в веб-сервисах', 'Настройка Nginx как балансировщика нагрузки', a.school_id, NOW()
FROM course c, account a
WHERE c.name = 'Сетевые технологии' AND a.nickname = 'alex.rom'
  AND NOT EXISTS (SELECT 1 FROM course_group cg WHERE cg.course_id = c.id AND cg.title = 'Балансировка нагрузки в веб-сервисах');

--changeset Diplom_Backend:2026-06-08-g10-m1 splitStatements:true endDelimiter:;
INSERT INTO course_group_member (group_id, account_id, is_owner, joined_at)
SELECT cg.id, a.id, TRUE, NOW()
FROM course_group cg JOIN course c ON cg.course_id = c.id, account a
WHERE c.name = 'Сетевые технологии' AND cg.title = 'Балансировка нагрузки в веб-сервисах' AND a.nickname = 'alex.rom'
  AND NOT EXISTS (SELECT 1 FROM course_group_member m WHERE m.group_id = cg.id AND m.account_id = a.id);

--changeset Diplom_Backend:2026-06-08-g10-m2 splitStatements:true endDelimiter:;
INSERT INTO course_group_member (group_id, account_id, is_owner, joined_at)
SELECT cg.id, a.id, FALSE, NOW()
FROM course_group cg JOIN course c ON cg.course_id = c.id, account a
WHERE c.name = 'Сетевые технологии' AND cg.title = 'Балансировка нагрузки в веб-сервисах' AND a.nickname = 'luda.grig'
  AND NOT EXISTS (SELECT 1 FROM course_group_member m WHERE m.group_id = cg.id AND m.account_id = a.id);

-- Слушание 1: ON_REVIEW
--changeset Diplom_Backend:2026-06-08-g10-h1 splitStatements:true endDelimiter:;
INSERT INTO hearing_submission (lesson_id, group_id, status, current_version, submitted_at, updated_at)
SELECT cl.id, cg.id, 'ON_REVIEW', 1, NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days'
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
     JOIN course_group cg ON cg.course_id = c.id
WHERE c.name = 'Сетевые технологии' AND cl.order_number = 1 AND cg.title = 'Балансировка нагрузки в веб-сервисах'
  AND NOT EXISTS (SELECT 1 FROM hearing_submission hs WHERE hs.lesson_id = cl.id AND hs.group_id = cg.id);
