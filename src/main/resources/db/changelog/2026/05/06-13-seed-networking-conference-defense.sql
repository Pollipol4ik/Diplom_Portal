--liquibase formatted sql

-- =============================================================================
-- Добавляем урок CONFERENCE_DEFENSE для курса «Сетевые технологии»
-- (в исходном сиде 04-03 этот этап отсутствовал — был только FINAL).
-- Затем добавляем критерии и все hearing_submission с рецензиями.
-- =============================================================================

-- ─── Урок CONFERENCE_DEFENSE ─────────────────────────────────────────────────

--changeset Diplom_Backend:2026-06-13-01-net-conf-lesson splitStatements:true endDelimiter:;
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description,
                           submission_type, order_number, max_score, category,
                           hearing_stage, hearing_open_for_students)
SELECT c.id,
       'Защита на конференции',
       'Финальная защита проекта перед экспертной комиссией на школьной конференции МосПолитех.',
       'Загрузите итоговую презентацию, пояснительную записку и все материалы проекта.',
       'FILE', 10, 45, 'HEARING', 'CONFERENCE_DEFENSE', TRUE
FROM course c
WHERE c.name = 'Сетевые технологии'
  AND NOT EXISTS (
      SELECT 1 FROM course_lesson cl
      WHERE cl.course_id = c.id AND cl.hearing_stage = 'CONFERENCE_DEFENSE'
  );

-- ─── Критерии оценивания (12 критериев МосПолитех) ───────────────────────────

--changeset Diplom_Backend:2026-06-13-02-net-crit-1 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 1, 'Актуальность выбранной проблемы',
       'Актуальность и значимость выбранной проблемы, наличие вариантов эффективного решения.', 3
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
WHERE c.name = 'Сетевые технологии' AND cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 1);

--changeset Diplom_Backend:2026-06-13-02-net-crit-2 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 2, 'Логичность и полнота представленных материалов',
       'Содержательность, информативность, глубина проработки темы, логическая завершённость.', 4
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
WHERE c.name = 'Сетевые технологии' AND cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 2);

--changeset Diplom_Backend:2026-06-13-02-net-crit-3 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 3, 'Практическая реализуемость работы',
       'Результаты работы имеют практическое значение и могут быть применены.', 6
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
WHERE c.name = 'Сетевые технологии' AND cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 3);

--changeset Diplom_Backend:2026-06-13-02-net-crit-4 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 4, 'Внедрение в практику',
       'Степень внедрения: наличие опытного образца, рабочей модели, апробация на целевой аудитории.', 5
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
WHERE c.name = 'Сетевые технологии' AND cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 4);

--changeset Diplom_Backend:2026-06-13-02-net-crit-5 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 5, 'Обоснование использованных методов',
       'Обоснование выбора методов исследования, технологий, применения современного оборудования.', 3
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
WHERE c.name = 'Сетевые технологии' AND cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 5);

--changeset Diplom_Backend:2026-06-13-02-net-crit-6 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 6, 'Применение практических навыков',
       'Изобретательность, техническая сложность, оригинальность, завершённость, качество выполнения.', 5
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
WHERE c.name = 'Сетевые технологии' AND cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 6);

--changeset Diplom_Backend:2026-06-13-02-net-crit-7 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 7, 'Самостоятельность выполнения работы',
       'Соответствие уровня материала уровню понимания на защите. Личный вклад участников.', 4
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
WHERE c.name = 'Сетевые технологии' AND cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 7);

--changeset Diplom_Backend:2026-06-13-02-net-crit-8 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 8, 'Умение аргументировать заключения и выводы',
       'Аргументированность выводов, опора на факты и теоретическую базу.', 4
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
WHERE c.name = 'Сетевые технологии' AND cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 8);

--changeset Diplom_Backend:2026-06-13-02-net-crit-9 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 9, 'Умение отвечать на вопросы',
       'Чёткость, обоснованность ответов на поставленные вопросы с использованием принятой терминологии.', 4
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
WHERE c.name = 'Сетевые технологии' AND cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 9);

--changeset Diplom_Backend:2026-06-13-02-net-crit-10 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 10, 'Культура публичного выступления',
       'Логика, грамотность изложения, ораторское мастерство, эмоциональность, внешний вид.', 3
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
WHERE c.name = 'Сетевые технологии' AND cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 10);

--changeset Diplom_Backend:2026-06-13-02-net-crit-11 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 11, 'Качество презентационных материалов',
       'Аккуратность, эстетика оформления, отсутствие грамматических ошибок.', 3
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
WHERE c.name = 'Сетевые технологии' AND cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 11);

--changeset Diplom_Backend:2026-06-13-02-net-crit-12 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 12, 'Наличие отзыва вуза/предприятия-партнёра',
       'Наличие отзыва, указывающего на полученный результат и дальнейшее развитие работы.', 1
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
WHERE c.name = 'Сетевые технологии' AND cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 12);

-- =============================================================================
-- HEARING SUBMISSIONS (теперь урок существует — вставляем по всем 10 группам)
-- =============================================================================

-- Группа 1: Чат-мессенджер — ACCEPTED
--changeset Diplom_Backend:2026-06-13-03-sub-g1 splitStatements:true endDelimiter:;
INSERT INTO hearing_submission (lesson_id, group_id, status, current_version, submitted_at, updated_at)
SELECT cl.id, cg.id, 'ACCEPTED', 1, NOW() - INTERVAL '10 days', NOW() - INTERVAL '5 days'
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
     JOIN course_group cg ON cg.course_id = c.id
WHERE c.name = 'Сетевые технологии' AND cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND cg.title = 'Чат-мессенджер в локальной сети'
  AND NOT EXISTS (SELECT 1 FROM hearing_submission hs WHERE hs.lesson_id = cl.id AND hs.group_id = cg.id);

-- Группа 2: Мониторинг — ON_REVIEW
--changeset Diplom_Backend:2026-06-13-03-sub-g2 splitStatements:true endDelimiter:;
INSERT INTO hearing_submission (lesson_id, group_id, status, current_version, submitted_at, updated_at)
SELECT cl.id, cg.id, 'ON_REVIEW', 1, NOW() - INTERVAL '3 days', NOW() - INTERVAL '3 days'
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
     JOIN course_group cg ON cg.course_id = c.id
WHERE c.name = 'Сетевые технологии' AND cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND cg.title = 'Система мониторинга сети'
  AND NOT EXISTS (SELECT 1 FROM hearing_submission hs WHERE hs.lesson_id = cl.id AND hs.group_id = cg.id);

-- Группа 3: Корпоративная сеть — NEEDS_REVISION
--changeset Diplom_Backend:2026-06-13-03-sub-g3 splitStatements:true endDelimiter:;
INSERT INTO hearing_submission (lesson_id, group_id, status, current_version, submitted_at, updated_at)
SELECT cl.id, cg.id, 'NEEDS_REVISION', 1, NOW() - INTERVAL '8 days', NOW() - INTERVAL '7 days'
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
     JOIN course_group cg ON cg.course_id = c.id
WHERE c.name = 'Сетевые технологии' AND cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND cg.title = 'Настройка корпоративной сети'
  AND NOT EXISTS (SELECT 1 FROM hearing_submission hs WHERE hs.lesson_id = cl.id AND hs.group_id = cg.id);

-- Группа 4: VPN-сервер — ACCEPTED
--changeset Diplom_Backend:2026-06-13-03-sub-g4 splitStatements:true endDelimiter:;
INSERT INTO hearing_submission (lesson_id, group_id, status, current_version, submitted_at, updated_at)
SELECT cl.id, cg.id, 'ACCEPTED', 1, NOW() - INTERVAL '12 days', NOW() - INTERVAL '6 days'
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
     JOIN course_group cg ON cg.course_id = c.id
WHERE c.name = 'Сетевые технологии' AND cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND cg.title = 'VPN-сервер для школы'
  AND NOT EXISTS (SELECT 1 FROM hearing_submission hs WHERE hs.lesson_id = cl.id AND hs.group_id = cg.id);

-- Группа 5: DNS и DHCP — ON_REVIEW
--changeset Diplom_Backend:2026-06-13-03-sub-g5 splitStatements:true endDelimiter:;
INSERT INTO hearing_submission (lesson_id, group_id, status, current_version, submitted_at, updated_at)
SELECT cl.id, cg.id, 'ON_REVIEW', 1, NOW() - INTERVAL '5 days', NOW() - INTERVAL '5 days'
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
     JOIN course_group cg ON cg.course_id = c.id
WHERE c.name = 'Сетевые технологии' AND cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND cg.title = 'Настройка DNS и DHCP-сервера'
  AND NOT EXISTS (SELECT 1 FROM hearing_submission hs WHERE hs.lesson_id = cl.id AND hs.group_id = cg.id);

-- Группа 6: Анализ трафика — REJECTED
--changeset Diplom_Backend:2026-06-13-03-sub-g6 splitStatements:true endDelimiter:;
INSERT INTO hearing_submission (lesson_id, group_id, status, current_version, submitted_at, updated_at)
SELECT cl.id, cg.id, 'REJECTED', 1, NOW() - INTERVAL '12 days', NOW() - INTERVAL '10 days'
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
     JOIN course_group cg ON cg.course_id = c.id
WHERE c.name = 'Сетевые технологии' AND cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND cg.title = 'Анализ трафика и сетевая безопасность'
  AND NOT EXISTS (SELECT 1 FROM hearing_submission hs WHERE hs.lesson_id = cl.id AND hs.group_id = cg.id);

-- Группа 7: RIP и OSPF — ACCEPTED (версия 2)
--changeset Diplom_Backend:2026-06-13-03-sub-g7 splitStatements:true endDelimiter:;
INSERT INTO hearing_submission (lesson_id, group_id, status, current_version, submitted_at, updated_at)
SELECT cl.id, cg.id, 'ACCEPTED', 2, NOW() - INTERVAL '14 days', NOW() - INTERVAL '3 days'
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
     JOIN course_group cg ON cg.course_id = c.id
WHERE c.name = 'Сетевые технологии' AND cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND cg.title = 'Протоколы маршрутизации RIP и OSPF'
  AND NOT EXISTS (SELECT 1 FROM hearing_submission hs WHERE hs.lesson_id = cl.id AND hs.group_id = cg.id);

-- Группа 8: Wi-Fi 802.1X — NEEDS_REVISION
--changeset Diplom_Backend:2026-06-13-03-sub-g8 splitStatements:true endDelimiter:;
INSERT INTO hearing_submission (lesson_id, group_id, status, current_version, submitted_at, updated_at)
SELECT cl.id, cg.id, 'NEEDS_REVISION', 1, NOW() - INTERVAL '9 days', NOW() - INTERVAL '8 days'
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
     JOIN course_group cg ON cg.course_id = c.id
WHERE c.name = 'Сетевые технологии' AND cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND cg.title = 'Wi-Fi с аутентификацией 802.1X'
  AND NOT EXISTS (SELECT 1 FROM hearing_submission hs WHERE hs.lesson_id = cl.id AND hs.group_id = cg.id);

-- Группа 9: Межсетевой экран — ACCEPTED
--changeset Diplom_Backend:2026-06-13-03-sub-g9 splitStatements:true endDelimiter:;
INSERT INTO hearing_submission (lesson_id, group_id, status, current_version, submitted_at, updated_at)
SELECT cl.id, cg.id, 'ACCEPTED', 1, NOW() - INTERVAL '11 days', NOW() - INTERVAL '6 days'
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
     JOIN course_group cg ON cg.course_id = c.id
WHERE c.name = 'Сетевые технологии' AND cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND cg.title = 'Настройка межсетевого экрана'
  AND NOT EXISTS (SELECT 1 FROM hearing_submission hs WHERE hs.lesson_id = cl.id AND hs.group_id = cg.id);

-- Группа 10: Балансировка нагрузки — ON_REVIEW
--changeset Diplom_Backend:2026-06-13-03-sub-g10 splitStatements:true endDelimiter:;
INSERT INTO hearing_submission (lesson_id, group_id, status, current_version, submitted_at, updated_at)
SELECT cl.id, cg.id, 'ON_REVIEW', 1, NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days'
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
     JOIN course_group cg ON cg.course_id = c.id
WHERE c.name = 'Сетевые технологии' AND cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND cg.title = 'Балансировка нагрузки в веб-сервисах'
  AND NOT EXISTS (SELECT 1 FROM hearing_submission hs WHERE hs.lesson_id = cl.id AND hs.group_id = cg.id);

-- =============================================================================
-- HEARING REVIEWS для принятых/проверенных работ
-- =============================================================================

-- Группа 1: Чат-мессенджер — ACCEPTED
--changeset Diplom_Backend:2026-06-13-04-rev-g1 splitStatements:true endDelimiter:;
INSERT INTO hearing_review (submission_id, moderator_id, comment, grade, submission_version, reviewed_at)
SELECT hs.id, a.id,
       'Отличная защита. Мессенджер продемонстрирован в работе, все вопросы комиссии освещены.',
       38, 1, NOW() - INTERVAL '5 days'
FROM hearing_submission hs
     JOIN course_lesson cl ON hs.lesson_id = cl.id
     JOIN course c ON cl.course_id = c.id
     JOIN course_group cg ON hs.group_id = cg.id
     , account a
WHERE c.name = 'Сетевые технологии' AND cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND cg.title = 'Чат-мессенджер в локальной сети' AND a.nickname = 'ant.vasiliev'
  AND NOT EXISTS (SELECT 1 FROM hearing_review hr WHERE hr.submission_id = hs.id AND hr.moderator_id = a.id);

-- Группа 4: VPN-сервер — ACCEPTED
--changeset Diplom_Backend:2026-06-13-04-rev-g4 splitStatements:true endDelimiter:;
INSERT INTO hearing_review (submission_id, moderator_id, comment, grade, submission_version, reviewed_at)
SELECT hs.id, a.id,
       'Хорошая работа. VPN с WireGuard полностью функционален, документация полная.',
       36, 1, NOW() - INTERVAL '6 days'
FROM hearing_submission hs
     JOIN course_lesson cl ON hs.lesson_id = cl.id
     JOIN course c ON cl.course_id = c.id
     JOIN course_group cg ON hs.group_id = cg.id
     , account a
WHERE c.name = 'Сетевые технологии' AND cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND cg.title = 'VPN-сервер для школы' AND a.nickname = 'pol.kuptsova'
  AND NOT EXISTS (SELECT 1 FROM hearing_review hr WHERE hr.submission_id = hs.id AND hr.moderator_id = a.id);

-- Группа 7: RIP и OSPF — ACCEPTED
--changeset Diplom_Backend:2026-06-13-04-rev-g7 splitStatements:true endDelimiter:;
INSERT INTO hearing_review (submission_id, moderator_id, comment, grade, submission_version, reviewed_at)
SELECT hs.id, a.id,
       'Глубокое понимание протоколов маршрутизации, стенд работает корректно.',
       40, 2, NOW() - INTERVAL '3 days'
FROM hearing_submission hs
     JOIN course_lesson cl ON hs.lesson_id = cl.id
     JOIN course c ON cl.course_id = c.id
     JOIN course_group cg ON hs.group_id = cg.id
     , account a
WHERE c.name = 'Сетевые технологии' AND cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND cg.title = 'Протоколы маршрутизации RIP и OSPF' AND a.nickname = 'ant.vasiliev'
  AND NOT EXISTS (SELECT 1 FROM hearing_review hr WHERE hr.submission_id = hs.id AND hr.moderator_id = a.id);

-- Группа 9: Межсетевой экран — ACCEPTED
--changeset Diplom_Backend:2026-06-13-04-rev-g9 splitStatements:true endDelimiter:;
INSERT INTO hearing_review (submission_id, moderator_id, comment, grade, submission_version, reviewed_at)
SELECT hs.id, a.id,
       'Правила iptables корректны и обоснованы, демонстрация убедительная.',
       37, 1, NOW() - INTERVAL '6 days'
FROM hearing_submission hs
     JOIN course_lesson cl ON hs.lesson_id = cl.id
     JOIN course c ON cl.course_id = c.id
     JOIN course_group cg ON hs.group_id = cg.id
     , account a
WHERE c.name = 'Сетевые технологии' AND cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND cg.title = 'Настройка межсетевого экрана' AND a.nickname = 'pol.kuptsova'
  AND NOT EXISTS (SELECT 1 FROM hearing_review hr WHERE hr.submission_id = hs.id AND hr.moderator_id = a.id);

-- Группа 3: Корпоративная сеть — NEEDS_REVISION (рецензия с замечаниями)
--changeset Diplom_Backend:2026-06-13-04-rev-g3 splitStatements:true endDelimiter:;
INSERT INTO hearing_review (submission_id, moderator_id, comment, grade, submission_version, reviewed_at)
SELECT hs.id, a.id,
       'Схема сети не полна, отсутствуют VLAN-теги. Требуется доработка топологии и пояснительной записки.',
       22, 1, NOW() - INTERVAL '7 days'
FROM hearing_submission hs
     JOIN course_lesson cl ON hs.lesson_id = cl.id
     JOIN course c ON cl.course_id = c.id
     JOIN course_group cg ON hs.group_id = cg.id
     , account a
WHERE c.name = 'Сетевые технологии' AND cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND cg.title = 'Настройка корпоративной сети' AND a.nickname = 'ant.vasiliev'
  AND NOT EXISTS (SELECT 1 FROM hearing_review hr WHERE hr.submission_id = hs.id AND hr.moderator_id = a.id);

-- Группа 8: Wi-Fi 802.1X — NEEDS_REVISION
--changeset Diplom_Backend:2026-06-13-04-rev-g8 splitStatements:true endDelimiter:;
INSERT INTO hearing_review (submission_id, moderator_id, comment, grade, submission_version, reviewed_at)
SELECT hs.id, a.id,
       'Настройка RADIUS сервера выполнена частично. Необходимо полностью протестировать аутентификацию клиентов.',
       20, 1, NOW() - INTERVAL '8 days'
FROM hearing_submission hs
     JOIN course_lesson cl ON hs.lesson_id = cl.id
     JOIN course c ON cl.course_id = c.id
     JOIN course_group cg ON hs.group_id = cg.id
     , account a
WHERE c.name = 'Сетевые технологии' AND cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND cg.title = 'Wi-Fi с аутентификацией 802.1X' AND a.nickname = 'pol.kuptsova'
  AND NOT EXISTS (SELECT 1 FROM hearing_review hr WHERE hr.submission_id = hs.id AND hr.moderator_id = a.id);

-- Группа 6: Анализ трафика — REJECTED
--changeset Diplom_Backend:2026-06-13-04-rev-g6 splitStatements:true endDelimiter:;
INSERT INTO hearing_review (submission_id, moderator_id, comment, grade, submission_version, reviewed_at)
SELECT hs.id, a.id,
       'Работа не соответствует требованиям: отсутствует анализ реального трафика, выводы не обоснованы.',
       NULL, 1, NOW() - INTERVAL '10 days'
FROM hearing_submission hs
     JOIN course_lesson cl ON hs.lesson_id = cl.id
     JOIN course c ON cl.course_id = c.id
     JOIN course_group cg ON hs.group_id = cg.id
     , account a
WHERE c.name = 'Сетевые технологии' AND cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND cg.title = 'Анализ трафика и сетевая безопасность' AND a.nickname = 'ant.vasiliev'
  AND NOT EXISTS (SELECT 1 FROM hearing_review hr WHERE hr.submission_id = hs.id AND hr.moderator_id = a.id);
