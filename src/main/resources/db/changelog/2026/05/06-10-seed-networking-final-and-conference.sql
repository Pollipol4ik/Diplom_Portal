--liquibase formatted sql

-- =============================================================================
-- Данные по курсу «Сетевые технологии»: слушания FINAL (порядок 9)
-- и CONFERENCE_DEFENSE.
--
-- Группы уже созданы в 06-08-seed-networking-submissions.sql.
-- Добавляем hearing_submission + hearing_review для каждой группы.
-- Модераторы: ant.vasiliev, pol.kuptsova (уже в базе).
-- =============================================================================

-- =============================================================================
-- ЧАСТЬ 1: ФИНАЛЬНЫЙ ПОКАЗ (hearing_stage = FINAL, order_number = 9)
-- =============================================================================

-- Группа 1: Чат-мессенджер — ACCEPTED
--changeset Diplom_Backend:2026-06-10-final-g1-sub splitStatements:true endDelimiter:;
INSERT INTO hearing_submission (lesson_id, group_id, status, current_version, submitted_at, updated_at)
SELECT cl.id, cg.id, 'ACCEPTED', 1, NOW() - INTERVAL '12 days', NOW() - INTERVAL '7 days'
FROM course_lesson cl
         JOIN course c ON cl.course_id = c.id
         JOIN course_group cg ON cg.course_id = c.id
WHERE c.name = 'Сетевые технологии'
  AND cl.hearing_stage = 'FINAL'
  AND cg.title = 'Чат-мессенджер в локальной сети'
  AND NOT EXISTS (SELECT 1 FROM hearing_submission hs WHERE hs.lesson_id = cl.id AND hs.group_id = cg.id);

--changeset Diplom_Backend:2026-06-10-final-g1-review splitStatements:true endDelimiter:;
INSERT INTO hearing_review (submission_id, moderator_id, comment, grade, submission_version, reviewed_at)
SELECT hs.id, a.id,
       'Финальный показ прошёл успешно. Мессенджер работает стабильно в локальной сети, продемонстрированы все заявленные функции.',
       28, 1, NOW() - INTERVAL '7 days'
FROM hearing_submission hs
         JOIN course_lesson cl ON hs.lesson_id = cl.id
         JOIN course c ON cl.course_id = c.id
         JOIN course_group cg ON hs.group_id = cg.id
   , account a
WHERE c.name = 'Сетевые технологии'
  AND cl.hearing_stage = 'FINAL'
  AND cg.title = 'Чат-мессенджер в локальной сети'
  AND a.nickname = 'ant.vasiliev'
  AND NOT EXISTS (SELECT 1 FROM hearing_review hr WHERE hr.submission_id = hs.id AND hr.moderator_id = a.id);

-- Группа 2: Мониторинг сети — NEEDS_REVISION
--changeset Diplom_Backend:2026-06-10-final-g2-sub splitStatements:true endDelimiter:;
INSERT INTO hearing_submission (lesson_id, group_id, status, current_version, submitted_at, updated_at)
SELECT cl.id, cg.id, 'NEEDS_REVISION', 1, NOW() - INTERVAL '8 days', NOW() - INTERVAL '4 days'
FROM course_lesson cl
         JOIN course c ON cl.course_id = c.id
         JOIN course_group cg ON cg.course_id = c.id
WHERE c.name = 'Сетевые технологии'
  AND cl.hearing_stage = 'FINAL'
  AND cg.title = 'Система мониторинга сети'
  AND NOT EXISTS (SELECT 1 FROM hearing_submission hs WHERE hs.lesson_id = cl.id AND hs.group_id = cg.id);

--changeset Diplom_Backend:2026-06-10-final-g2-review splitStatements:true endDelimiter:;
INSERT INTO hearing_review (submission_id, moderator_id, comment, grade, submission_version, reviewed_at)
SELECT hs.id, a.id,
       'Система работает, но алерты не доведены до конца. Необходимо доработать уведомления и добавить графики исторических данных.',
       18, 1, NOW() - INTERVAL '4 days'
FROM hearing_submission hs
         JOIN course_lesson cl ON hs.lesson_id = cl.id
         JOIN course c ON cl.course_id = c.id
         JOIN course_group cg ON hs.group_id = cg.id
   , account a
WHERE c.name = 'Сетевые технологии'
  AND cl.hearing_stage = 'FINAL'
  AND cg.title = 'Система мониторинга сети'
  AND a.nickname = 'pol.kuptsova'
  AND NOT EXISTS (SELECT 1 FROM hearing_review hr WHERE hr.submission_id = hs.id AND hr.moderator_id = a.id);

-- Группа 3: VPN-сервер — ACCEPTED
--changeset Diplom_Backend:2026-06-10-final-g3-sub splitStatements:true endDelimiter:;
INSERT INTO hearing_submission (lesson_id, group_id, status, current_version, submitted_at, updated_at)
SELECT cl.id, cg.id, 'ACCEPTED', 1, NOW() - INTERVAL '10 days', NOW() - INTERVAL '5 days'
FROM course_lesson cl
         JOIN course c ON cl.course_id = c.id
         JOIN course_group cg ON cg.course_id = c.id
WHERE c.name = 'Сетевые технологии'
  AND cl.hearing_stage = 'FINAL'
  AND cg.title = 'VPN-сервер для удалённого доступа'
  AND NOT EXISTS (SELECT 1 FROM hearing_submission hs WHERE hs.lesson_id = cl.id AND hs.group_id = cg.id);

--changeset Diplom_Backend:2026-06-10-final-g3-review splitStatements:true endDelimiter:;
INSERT INTO hearing_review (submission_id, moderator_id, comment, grade, submission_version, reviewed_at)
SELECT hs.id, a.id,
       'Отличная работа: WireGuard настроен корректно, туннелирование через NAT реализовано. Документация полная.',
       27, 1, NOW() - INTERVAL '5 days'
FROM hearing_submission hs
         JOIN course_lesson cl ON hs.lesson_id = cl.id
         JOIN course c ON cl.course_id = c.id
         JOIN course_group cg ON hs.group_id = cg.id
   , account a
WHERE c.name = 'Сетевые технологии'
  AND cl.hearing_stage = 'FINAL'
  AND cg.title = 'VPN-сервер для удалённого доступа'
  AND a.nickname = 'ant.vasiliev'
  AND NOT EXISTS (SELECT 1 FROM hearing_review hr WHERE hr.submission_id = hs.id AND hr.moderator_id = a.id);

-- Группа 4: DNS и DHCP — ON_REVIEW
--changeset Diplom_Backend:2026-06-10-final-g4-sub splitStatements:true endDelimiter:;
INSERT INTO hearing_submission (lesson_id, group_id, status, current_version, submitted_at, updated_at)
SELECT cl.id, cg.id, 'ON_REVIEW', 1, NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days'
FROM course_lesson cl
         JOIN course c ON cl.course_id = c.id
         JOIN course_group cg ON cg.course_id = c.id
WHERE c.name = 'Сетевые технологии'
  AND cl.hearing_stage = 'FINAL'
  AND cg.title = 'DNS и DHCP сервер для школьной сети'
  AND NOT EXISTS (SELECT 1 FROM hearing_submission hs WHERE hs.lesson_id = cl.id AND hs.group_id = cg.id);

-- Группа 5: Honeypot — ACCEPTED
--changeset Diplom_Backend:2026-06-10-final-g5-sub splitStatements:true endDelimiter:;
INSERT INTO hearing_submission (lesson_id, group_id, status, current_version, submitted_at, updated_at)
SELECT cl.id, cg.id, 'ACCEPTED', 1, NOW() - INTERVAL '9 days', NOW() - INTERVAL '4 days'
FROM course_lesson cl
         JOIN course c ON cl.course_id = c.id
         JOIN course_group cg ON cg.course_id = c.id
WHERE c.name = 'Сетевые технологии'
  AND cl.hearing_stage = 'FINAL'
  AND cg.title = 'Ловушка для сетевых атак (Honeypot)'
  AND NOT EXISTS (SELECT 1 FROM hearing_submission hs WHERE hs.lesson_id = cl.id AND hs.group_id = cg.id);

--changeset Diplom_Backend:2026-06-10-final-g5-review splitStatements:true endDelimiter:;
INSERT INTO hearing_review (submission_id, moderator_id, comment, grade, submission_version, reviewed_at)
SELECT hs.id, a.id,
       'Honeypot реализован на базе Cowrie, логирование настроено. Продемонстрировано несколько типов атак.',
       26, 1, NOW() - INTERVAL '4 days'
FROM hearing_submission hs
         JOIN course_lesson cl ON hs.lesson_id = cl.id
         JOIN course c ON cl.course_id = c.id
         JOIN course_group cg ON hs.group_id = cg.id
   , account a
WHERE c.name = 'Сетевые технологии'
  AND cl.hearing_stage = 'FINAL'
  AND cg.title = 'Ловушка для сетевых атак (Honeypot)'
  AND a.nickname = 'pol.kuptsova'
  AND NOT EXISTS (SELECT 1 FROM hearing_review hr WHERE hr.submission_id = hs.id AND hr.moderator_id = a.id);

-- Группа 6: VLAN — ACCEPTED
--changeset Diplom_Backend:2026-06-10-final-g6-sub splitStatements:true endDelimiter:;
INSERT INTO hearing_submission (lesson_id, group_id, status, current_version, submitted_at, updated_at)
SELECT cl.id, cg.id, 'ACCEPTED', 1, NOW() - INTERVAL '11 days', NOW() - INTERVAL '6 days'
FROM course_lesson cl
         JOIN course c ON cl.course_id = c.id
         JOIN course_group cg ON cg.course_id = c.id
WHERE c.name = 'Сетевые технологии'
  AND cl.hearing_stage = 'FINAL'
  AND cg.title = 'Сегментация сети с помощью VLAN'
  AND NOT EXISTS (SELECT 1 FROM hearing_submission hs WHERE hs.lesson_id = cl.id AND hs.group_id = cg.id);

--changeset Diplom_Backend:2026-06-10-final-g6-review splitStatements:true endDelimiter:;
INSERT INTO hearing_review (submission_id, moderator_id, comment, grade, submission_version, reviewed_at)
SELECT hs.id, a.id,
       'VLAN сегментация реализована корректно. Топология сети наглядна, конфигурация коммутаторов задокументирована.',
       25, 1, NOW() - INTERVAL '6 days'
FROM hearing_submission hs
         JOIN course_lesson cl ON hs.lesson_id = cl.id
         JOIN course c ON cl.course_id = c.id
         JOIN course_group cg ON hs.group_id = cg.id
   , account a
WHERE c.name = 'Сетевые технологии'
  AND cl.hearing_stage = 'FINAL'
  AND cg.title = 'Сегментация сети с помощью VLAN'
  AND a.nickname = 'ant.vasiliev'
  AND NOT EXISTS (SELECT 1 FROM hearing_review hr WHERE hr.submission_id = hs.id AND hr.moderator_id = a.id);

-- Группа 7: RIP и OSPF — ACCEPTED
--changeset Diplom_Backend:2026-06-10-final-g7-sub splitStatements:true endDelimiter:;
INSERT INTO hearing_submission (lesson_id, group_id, status, current_version, submitted_at, updated_at)
SELECT cl.id, cg.id, 'ACCEPTED', 1, NOW() - INTERVAL '13 days', NOW() - INTERVAL '8 days'
FROM course_lesson cl
         JOIN course c ON cl.course_id = c.id
         JOIN course_group cg ON cg.course_id = c.id
WHERE c.name = 'Сетевые технологии'
  AND cl.hearing_stage = 'FINAL'
  AND cg.title = 'Протоколы маршрутизации RIP и OSPF'
  AND NOT EXISTS (SELECT 1 FROM hearing_submission hs WHERE hs.lesson_id = cl.id AND hs.group_id = cg.id);

--changeset Diplom_Backend:2026-06-10-final-g7-review splitStatements:true endDelimiter:;
INSERT INTO hearing_review (submission_id, moderator_id, comment, grade, submission_version, reviewed_at)
SELECT hs.id, a.id,
       'Сравнительный анализ RIP и OSPF выполнен, стенд с эмуляцией топологии работает. Результаты измерений корректны.',
       27, 1, NOW() - INTERVAL '8 days'
FROM hearing_submission hs
         JOIN course_lesson cl ON hs.lesson_id = cl.id
         JOIN course c ON cl.course_id = c.id
         JOIN course_group cg ON hs.group_id = cg.id
   , account a
WHERE c.name = 'Сетевые технологии'
  AND cl.hearing_stage = 'FINAL'
  AND cg.title = 'Протоколы маршрутизации RIP и OSPF'
  AND a.nickname = 'pol.kuptsova'
  AND NOT EXISTS (SELECT 1 FROM hearing_review hr WHERE hr.submission_id = hs.id AND hr.moderator_id = a.id);

-- Группа 8: Wi-Fi 802.1X — ON_REVIEW
--changeset Diplom_Backend:2026-06-10-final-g8-sub splitStatements:true endDelimiter:;
INSERT INTO hearing_submission (lesson_id, group_id, status, current_version, submitted_at, updated_at)
SELECT cl.id, cg.id, 'ON_REVIEW', 1, NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day'
FROM course_lesson cl
         JOIN course c ON cl.course_id = c.id
         JOIN course_group cg ON cg.course_id = c.id
WHERE c.name = 'Сетевые технологии'
  AND cl.hearing_stage = 'FINAL'
  AND cg.title = 'Wi-Fi с аутентификацией 802.1X'
  AND NOT EXISTS (SELECT 1 FROM hearing_submission hs WHERE hs.lesson_id = cl.id AND hs.group_id = cg.id);

-- Группа 9: Межсетевой экран — ACCEPTED
--changeset Diplom_Backend:2026-06-10-final-g9-sub splitStatements:true endDelimiter:;
INSERT INTO hearing_submission (lesson_id, group_id, status, current_version, submitted_at, updated_at)
SELECT cl.id, cg.id, 'ACCEPTED', 1, NOW() - INTERVAL '10 days', NOW() - INTERVAL '5 days'
FROM course_lesson cl
         JOIN course c ON cl.course_id = c.id
         JOIN course_group cg ON cg.course_id = c.id
WHERE c.name = 'Сетевые технологии'
  AND cl.hearing_stage = 'FINAL'
  AND cg.title = 'Настройка межсетевого экрана'
  AND NOT EXISTS (SELECT 1 FROM hearing_submission hs WHERE hs.lesson_id = cl.id AND hs.group_id = cg.id);

--changeset Diplom_Backend:2026-06-10-final-g9-review splitStatements:true endDelimiter:;
INSERT INTO hearing_review (submission_id, moderator_id, comment, grade, submission_version, reviewed_at)
SELECT hs.id, a.id,
       'Правила iptables корректны, сценарии атак и защиты продемонстрированы. Отчёт оформлен по стандарту.',
       26, 1, NOW() - INTERVAL '5 days'
FROM hearing_submission hs
         JOIN course_lesson cl ON hs.lesson_id = cl.id
         JOIN course c ON cl.course_id = c.id
         JOIN course_group cg ON hs.group_id = cg.id
   , account a
WHERE c.name = 'Сетевые технологии'
  AND cl.hearing_stage = 'FINAL'
  AND cg.title = 'Настройка межсетевого экрана'
  AND a.nickname = 'ant.vasiliev'
  AND NOT EXISTS (SELECT 1 FROM hearing_review hr WHERE hr.submission_id = hs.id AND hr.moderator_id = a.id);

-- =============================================================================
-- ЧАСТЬ 2: Открыть FINAL и CONFERENCE_DEFENSE для Сетевых технологий
-- =============================================================================

--changeset Diplom_Backend:2026-06-10-open-final splitStatements:true endDelimiter:;
UPDATE course_lesson
SET hearing_open_for_students = TRUE
WHERE hearing_stage = 'FINAL'
  AND course_id = (SELECT id FROM course WHERE name = 'Сетевые технологии')
  AND hearing_open_for_students = FALSE;

--changeset Diplom_Backend:2026-06-10-open-conference splitStatements:true endDelimiter:;
UPDATE course_lesson
SET hearing_open_for_students = TRUE
WHERE hearing_stage = 'CONFERENCE_DEFENSE'
  AND course_id = (SELECT id FROM course WHERE name = 'Сетевые технологии')
  AND hearing_open_for_students = FALSE;