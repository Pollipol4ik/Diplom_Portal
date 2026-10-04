--liquibase formatted sql

-- =============================================================================
-- МИГРАЦИЯ 07-01: ПОЛНЫЙ НАБОР ДАННЫХ ДЛЯ ОЦЕНИВАНИЯ
--
-- Содержит:
--   1. Критерии для HEARING-уроков (TOPIC_APPROVAL, INTERMEDIATE, FINAL)
--      — у CONFERENCE_DEFENSE критерии уже заданы в 04-04 / 06-09
--   2. submission_criterion_grade — баллы по критериям для ACCEPTED lesson_submission
--      (целевые ИТ-курсы: уроки 2,3,4,5,7,8; вводные курсы: уроки 1,2,3;
--       курсы для отстающих: уроки 1,2,3,4)
--   3. submission_review_history — рецензии модераторов для работ по Сетевым технологиям
--      (дополняет частичные записи из 06-12)
-- Все INSERT идемпотентны через NOT EXISTS.
-- =============================================================================


-- =============================================================================
-- ═══ ЧАСТЬ 1: КРИТЕРИИ ДЛЯ HEARING-УРОКОВ ════════════════════════════════════
-- =============================================================================
-- TOPIC_APPROVAL (слушание 1, 10б = 4+3+3): оценивается как заявка на тему
-- Для каждого курса урок order_number=1, category='HEARING'
-- =============================================================================

-- ── TOPIC_APPROVAL: критерий 1 — Актуальность и обоснованность темы ──────────
--changeset Diplom_Backend:2026-07-01-01 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 1,
       'Актуальность и обоснованность темы',
       'Тема проекта актуальна, её актуальность аргументирована. Указаны проблема, которую решает проект, и целевая аудитория.',
       4
FROM course_lesson cl
WHERE cl.hearing_stage = 'TOPIC_APPROVAL'
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 1);

-- ── TOPIC_APPROVAL: критерий 2 — Чёткость формулировки цели и задач ──────────
--changeset Diplom_Backend:2026-07-01-02 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 2,
       'Чёткость формулировки цели и задач',
       'Цель сформулирована конкретно и достижимо. Задачи логически вытекают из цели и разбиты на этапы.',
       3
FROM course_lesson cl
WHERE cl.hearing_stage = 'TOPIC_APPROVAL'
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 2);

-- ── TOPIC_APPROVAL: критерий 3 — Реализуемость в рамках учебного года ────────
--changeset Diplom_Backend:2026-07-01-03 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 3,
       'Реализуемость в рамках учебного года',
       'Проект реально выполнить в отведённые сроки. Указаны необходимые ресурсы, инструменты, технологии.',
       3
FROM course_lesson cl
WHERE cl.hearing_stage = 'TOPIC_APPROVAL'
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 3);

-- ── Обновляем max_score для TOPIC_APPROVAL уроков ────────────────────────────
--changeset Diplom_Backend:2026-07-01-04 splitStatements:true endDelimiter:;
UPDATE course_lesson
SET max_score = 10
WHERE hearing_stage = 'TOPIC_APPROVAL'
  AND max_score != 10;


-- =============================================================================
-- INTERMEDIATE (слушание 6, 20б = 7+6+7): промежуточный показ прогресса
-- =============================================================================

-- ── INTERMEDIATE: критерий 1 — Выполнение плана работ ────────────────────────
--changeset Diplom_Backend:2026-07-01-05 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 1,
       'Выполнение плана работ',
       'Выполнено не менее 50% запланированного объёма работ. Соблюдаются контрольные точки и сроки этапов.',
       7
FROM course_lesson cl
WHERE cl.hearing_stage = 'INTERMEDIATE'
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 1);

-- ── INTERMEDIATE: критерий 2 — Качество промежуточных результатов ────────────
--changeset Diplom_Backend:2026-07-01-06 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 2,
       'Качество промежуточных результатов',
       'Представленные материалы демонстрируют понимание темы. Прототип/макет/схема соответствуют заявленной концепции.',
       6
FROM course_lesson cl
WHERE cl.hearing_stage = 'INTERMEDIATE'
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 2);

-- ── INTERMEDIATE: критерий 3 — Самостоятельность и командная работа ──────────
--changeset Diplom_Backend:2026-07-01-07 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 3,
       'Самостоятельность и командная работа',
       'Участники демонстрируют личный вклад и понимание проекта. Обязанности распределены, есть признаки командной координации.',
       7
FROM course_lesson cl
WHERE cl.hearing_stage = 'INTERMEDIATE'
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 3);

-- ── Обновляем max_score для INTERMEDIATE уроков ───────────────────────────────
--changeset Diplom_Backend:2026-07-01-08 splitStatements:true endDelimiter:;
UPDATE course_lesson
SET max_score = 20
WHERE hearing_stage = 'INTERMEDIATE'
  AND max_score != 20;


-- =============================================================================
-- FINAL (слушание 9, 30б = 10+10+10): финальный показ до конференции
-- =============================================================================

-- ── FINAL: критерий 1 — Завершённость проекта ────────────────────────────────
--changeset Diplom_Backend:2026-07-01-09 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 1,
       'Завершённость проекта',
       'Проект завершён в полном объёме: все задачи выполнены, результат работоспособен и соответствует заявленной цели.',
       10
FROM course_lesson cl
WHERE cl.hearing_stage = 'FINAL'
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 1);

-- ── FINAL: критерий 2 — Качество пояснительной записки и презентации ─────────
--changeset Diplom_Backend:2026-07-01-10 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 2,
       'Качество пояснительной записки и презентации',
       'Пояснительная записка оформлена по требованиям (структура, ГОСТ). Презентация информативна, логична, визуально корректна.',
       10
FROM course_lesson cl
WHERE cl.hearing_stage = 'FINAL'
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 2);

-- ── FINAL: критерий 3 — Владение материалом и защита ─────────────────────────
--changeset Diplom_Backend:2026-07-01-11 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 3,
       'Владение материалом и защита',
       'Участники уверенно отвечают на вопросы, обосновывают решения, демонстрируют глубокое понимание темы.',
       10
FROM course_lesson cl
WHERE cl.hearing_stage = 'FINAL'
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 3);

-- ── Обновляем max_score для FINAL уроков ─────────────────────────────────────
--changeset Diplom_Backend:2026-07-01-12 splitStatements:true endDelimiter:;
UPDATE course_lesson
SET max_score = 30
WHERE hearing_stage = 'FINAL'
  AND max_score != 30;


-- =============================================================================
-- ═══ ЧАСТЬ 2: БАЛЛЫ ПО КРИТЕРИЯМ (submission_criterion_grade) ════════════════
-- =============================================================================
-- Для всех ACCEPTED lesson_submission проставляем оценки по критериям.
-- Логика: score урока = сумма points по критериям.
-- Критериев 3 штуки на урок; распределяем баллы пропорционально max_points.
-- ПРИНЦИП: если score == max_score урока → каждый критерий = max_points
--           иначе распределяем proportionally (критерий 1 получает остаток)
-- Для простоты seed-данных: у ACCEPTED работ score == max_score,
-- поэтому каждый критерий = max_points (полный балл).
-- =============================================================================

-- ─────────────────────────────────────────────────────────────────────────────
-- БЛОК 2А: Целевые ИТ-курсы — уроки 2,3,4 (по 10 б = критерии 3+4+3)
-- ─────────────────────────────────────────────────────────────────────────────

-- Критерий 1 (3 б) для урока 2 всех ACCEPTED работ
--changeset Diplom_Backend:2026-07-01-g01 splitStatements:true endDelimiter:;
INSERT INTO submission_criterion_grade (submission_id, criterion_id, points)
SELECT ls.id, gc.id, gc.max_points
FROM lesson_submission ls
         JOIN course_lesson cl ON ls.lesson_id = cl.id
         JOIN grading_criterion gc ON gc.lesson_id = cl.id AND gc.order_number = 1
         JOIN course c ON cl.course_id = c.id
WHERE ls.status = 'ACCEPTED'
  AND cl.order_number = 2
  AND cl.category = 'LESSON'
  AND c.for_lagging_students = FALSE AND c.is_introduction = FALSE
  AND NOT EXISTS (SELECT 1 FROM submission_criterion_grade scg
                  WHERE scg.submission_id = ls.id AND scg.criterion_id = gc.id);

--changeset Diplom_Backend:2026-07-01-g02 splitStatements:true endDelimiter:;
INSERT INTO submission_criterion_grade (submission_id, criterion_id, points)
SELECT ls.id, gc.id, gc.max_points
FROM lesson_submission ls
         JOIN course_lesson cl ON ls.lesson_id = cl.id
         JOIN grading_criterion gc ON gc.lesson_id = cl.id AND gc.order_number = 2
         JOIN course c ON cl.course_id = c.id
WHERE ls.status = 'ACCEPTED'
  AND cl.order_number = 2
  AND cl.category = 'LESSON'
  AND c.for_lagging_students = FALSE AND c.is_introduction = FALSE
  AND NOT EXISTS (SELECT 1 FROM submission_criterion_grade scg
                  WHERE scg.submission_id = ls.id AND scg.criterion_id = gc.id);

--changeset Diplom_Backend:2026-07-01-g03 splitStatements:true endDelimiter:;
INSERT INTO submission_criterion_grade (submission_id, criterion_id, points)
SELECT ls.id, gc.id, gc.max_points
FROM lesson_submission ls
         JOIN course_lesson cl ON ls.lesson_id = cl.id
         JOIN grading_criterion gc ON gc.lesson_id = cl.id AND gc.order_number = 3
         JOIN course c ON cl.course_id = c.id
WHERE ls.status = 'ACCEPTED'
  AND cl.order_number = 2
  AND cl.category = 'LESSON'
  AND c.for_lagging_students = FALSE AND c.is_introduction = FALSE
  AND NOT EXISTS (SELECT 1 FROM submission_criterion_grade scg
                  WHERE scg.submission_id = ls.id AND scg.criterion_id = gc.id);

-- Урок 3 (10 б = 3+4+3)
--changeset Diplom_Backend:2026-07-01-g04 splitStatements:true endDelimiter:;
INSERT INTO submission_criterion_grade (submission_id, criterion_id, points)
SELECT ls.id, gc.id, gc.max_points
FROM lesson_submission ls
         JOIN course_lesson cl ON ls.lesson_id = cl.id
         JOIN grading_criterion gc ON gc.lesson_id = cl.id AND gc.order_number = 1
         JOIN course c ON cl.course_id = c.id
WHERE ls.status = 'ACCEPTED'
  AND cl.order_number = 3
  AND cl.category = 'LESSON'
  AND c.for_lagging_students = FALSE AND c.is_introduction = FALSE
  AND NOT EXISTS (SELECT 1 FROM submission_criterion_grade scg
                  WHERE scg.submission_id = ls.id AND scg.criterion_id = gc.id);

--changeset Diplom_Backend:2026-07-01-g05 splitStatements:true endDelimiter:;
INSERT INTO submission_criterion_grade (submission_id, criterion_id, points)
SELECT ls.id, gc.id, gc.max_points
FROM lesson_submission ls
         JOIN course_lesson cl ON ls.lesson_id = cl.id
         JOIN grading_criterion gc ON gc.lesson_id = cl.id AND gc.order_number = 2
         JOIN course c ON cl.course_id = c.id
WHERE ls.status = 'ACCEPTED'
  AND cl.order_number = 3
  AND cl.category = 'LESSON'
  AND c.for_lagging_students = FALSE AND c.is_introduction = FALSE
  AND NOT EXISTS (SELECT 1 FROM submission_criterion_grade scg
                  WHERE scg.submission_id = ls.id AND scg.criterion_id = gc.id);

--changeset Diplom_Backend:2026-07-01-g06 splitStatements:true endDelimiter:;
INSERT INTO submission_criterion_grade (submission_id, criterion_id, points)
SELECT ls.id, gc.id, gc.max_points
FROM lesson_submission ls
         JOIN course_lesson cl ON ls.lesson_id = cl.id
         JOIN grading_criterion gc ON gc.lesson_id = cl.id AND gc.order_number = 3
         JOIN course c ON cl.course_id = c.id
WHERE ls.status = 'ACCEPTED'
  AND cl.order_number = 3
  AND cl.category = 'LESSON'
  AND c.for_lagging_students = FALSE AND c.is_introduction = FALSE
  AND NOT EXISTS (SELECT 1 FROM submission_criterion_grade scg
                  WHERE scg.submission_id = ls.id AND scg.criterion_id = gc.id);

-- Урок 4 (10 б = 4+3+3)
--changeset Diplom_Backend:2026-07-01-g07 splitStatements:true endDelimiter:;
INSERT INTO submission_criterion_grade (submission_id, criterion_id, points)
SELECT ls.id, gc.id, gc.max_points
FROM lesson_submission ls
         JOIN course_lesson cl ON ls.lesson_id = cl.id
         JOIN grading_criterion gc ON gc.lesson_id = cl.id AND gc.order_number = 1
         JOIN course c ON cl.course_id = c.id
WHERE ls.status = 'ACCEPTED'
  AND cl.order_number = 4
  AND cl.category = 'LESSON'
  AND c.for_lagging_students = FALSE AND c.is_introduction = FALSE
  AND NOT EXISTS (SELECT 1 FROM submission_criterion_grade scg
                  WHERE scg.submission_id = ls.id AND scg.criterion_id = gc.id);

--changeset Diplom_Backend:2026-07-01-g08 splitStatements:true endDelimiter:;
INSERT INTO submission_criterion_grade (submission_id, criterion_id, points)
SELECT ls.id, gc.id, gc.max_points
FROM lesson_submission ls
         JOIN course_lesson cl ON ls.lesson_id = cl.id
         JOIN grading_criterion gc ON gc.lesson_id = cl.id AND gc.order_number = 2
         JOIN course c ON cl.course_id = c.id
WHERE ls.status = 'ACCEPTED'
  AND cl.order_number = 4
  AND cl.category = 'LESSON'
  AND c.for_lagging_students = FALSE AND c.is_introduction = FALSE
  AND NOT EXISTS (SELECT 1 FROM submission_criterion_grade scg
                  WHERE scg.submission_id = ls.id AND scg.criterion_id = gc.id);

--changeset Diplom_Backend:2026-07-01-g09 splitStatements:true endDelimiter:;
INSERT INTO submission_criterion_grade (submission_id, criterion_id, points)
SELECT ls.id, gc.id, gc.max_points
FROM lesson_submission ls
         JOIN course_lesson cl ON ls.lesson_id = cl.id
         JOIN grading_criterion gc ON gc.lesson_id = cl.id AND gc.order_number = 3
         JOIN course c ON cl.course_id = c.id
WHERE ls.status = 'ACCEPTED'
  AND cl.order_number = 4
  AND cl.category = 'LESSON'
  AND c.for_lagging_students = FALSE AND c.is_introduction = FALSE
  AND NOT EXISTS (SELECT 1 FROM submission_criterion_grade scg
                  WHERE scg.submission_id = ls.id AND scg.criterion_id = gc.id);

-- Урок 5 (15 б = 5+5+5)
--changeset Diplom_Backend:2026-07-01-g10 splitStatements:true endDelimiter:;
INSERT INTO submission_criterion_grade (submission_id, criterion_id, points)
SELECT ls.id, gc.id, gc.max_points
FROM lesson_submission ls
         JOIN course_lesson cl ON ls.lesson_id = cl.id
         JOIN grading_criterion gc ON gc.lesson_id = cl.id AND gc.order_number = 1
         JOIN course c ON cl.course_id = c.id
WHERE ls.status = 'ACCEPTED'
  AND cl.order_number = 5
  AND cl.category = 'LESSON'
  AND c.for_lagging_students = FALSE AND c.is_introduction = FALSE
  AND NOT EXISTS (SELECT 1 FROM submission_criterion_grade scg
                  WHERE scg.submission_id = ls.id AND scg.criterion_id = gc.id);

--changeset Diplom_Backend:2026-07-01-g11 splitStatements:true endDelimiter:;
INSERT INTO submission_criterion_grade (submission_id, criterion_id, points)
SELECT ls.id, gc.id, gc.max_points
FROM lesson_submission ls
         JOIN course_lesson cl ON ls.lesson_id = cl.id
         JOIN grading_criterion gc ON gc.lesson_id = cl.id AND gc.order_number = 2
         JOIN course c ON cl.course_id = c.id
WHERE ls.status = 'ACCEPTED'
  AND cl.order_number = 5
  AND cl.category = 'LESSON'
  AND c.for_lagging_students = FALSE AND c.is_introduction = FALSE
  AND NOT EXISTS (SELECT 1 FROM submission_criterion_grade scg
                  WHERE scg.submission_id = ls.id AND scg.criterion_id = gc.id);

--changeset Diplom_Backend:2026-07-01-g12 splitStatements:true endDelimiter:;
INSERT INTO submission_criterion_grade (submission_id, criterion_id, points)
SELECT ls.id, gc.id, gc.max_points
FROM lesson_submission ls
         JOIN course_lesson cl ON ls.lesson_id = cl.id
         JOIN grading_criterion gc ON gc.lesson_id = cl.id AND gc.order_number = 3
         JOIN course c ON cl.course_id = c.id
WHERE ls.status = 'ACCEPTED'
  AND cl.order_number = 5
  AND cl.category = 'LESSON'
  AND c.for_lagging_students = FALSE AND c.is_introduction = FALSE
  AND NOT EXISTS (SELECT 1 FROM submission_criterion_grade scg
                  WHERE scg.submission_id = ls.id AND scg.criterion_id = gc.id);

-- Урок 7 (10 б = 3+4+3)
--changeset Diplom_Backend:2026-07-01-g13 splitStatements:true endDelimiter:;
INSERT INTO submission_criterion_grade (submission_id, criterion_id, points)
SELECT ls.id, gc.id, gc.max_points
FROM lesson_submission ls
         JOIN course_lesson cl ON ls.lesson_id = cl.id
         JOIN grading_criterion gc ON gc.lesson_id = cl.id AND gc.order_number = 1
         JOIN course c ON cl.course_id = c.id
WHERE ls.status = 'ACCEPTED'
  AND cl.order_number = 7
  AND cl.category = 'LESSON'
  AND c.for_lagging_students = FALSE AND c.is_introduction = FALSE
  AND NOT EXISTS (SELECT 1 FROM submission_criterion_grade scg
                  WHERE scg.submission_id = ls.id AND scg.criterion_id = gc.id);

--changeset Diplom_Backend:2026-07-01-g14 splitStatements:true endDelimiter:;
INSERT INTO submission_criterion_grade (submission_id, criterion_id, points)
SELECT ls.id, gc.id, gc.max_points
FROM lesson_submission ls
         JOIN course_lesson cl ON ls.lesson_id = cl.id
         JOIN grading_criterion gc ON gc.lesson_id = cl.id AND gc.order_number = 2
         JOIN course c ON cl.course_id = c.id
WHERE ls.status = 'ACCEPTED'
  AND cl.order_number = 7
  AND cl.category = 'LESSON'
  AND c.for_lagging_students = FALSE AND c.is_introduction = FALSE
  AND NOT EXISTS (SELECT 1 FROM submission_criterion_grade scg
                  WHERE scg.submission_id = ls.id AND scg.criterion_id = gc.id);

--changeset Diplom_Backend:2026-07-01-g15 splitStatements:true endDelimiter:;
INSERT INTO submission_criterion_grade (submission_id, criterion_id, points)
SELECT ls.id, gc.id, gc.max_points
FROM lesson_submission ls
         JOIN course_lesson cl ON ls.lesson_id = cl.id
         JOIN grading_criterion gc ON gc.lesson_id = cl.id AND gc.order_number = 3
         JOIN course c ON cl.course_id = c.id
WHERE ls.status = 'ACCEPTED'
  AND cl.order_number = 7
  AND cl.category = 'LESSON'
  AND c.for_lagging_students = FALSE AND c.is_introduction = FALSE
  AND NOT EXISTS (SELECT 1 FROM submission_criterion_grade scg
                  WHERE scg.submission_id = ls.id AND scg.criterion_id = gc.id);

-- Урок 8 (15 б = 5+5+5)
--changeset Diplom_Backend:2026-07-01-g16 splitStatements:true endDelimiter:;
INSERT INTO submission_criterion_grade (submission_id, criterion_id, points)
SELECT ls.id, gc.id, gc.max_points
FROM lesson_submission ls
         JOIN course_lesson cl ON ls.lesson_id = cl.id
         JOIN grading_criterion gc ON gc.lesson_id = cl.id AND gc.order_number = 1
         JOIN course c ON cl.course_id = c.id
WHERE ls.status = 'ACCEPTED'
  AND cl.order_number = 8
  AND cl.category = 'LESSON'
  AND c.for_lagging_students = FALSE AND c.is_introduction = FALSE
  AND NOT EXISTS (SELECT 1 FROM submission_criterion_grade scg
                  WHERE scg.submission_id = ls.id AND scg.criterion_id = gc.id);

--changeset Diplom_Backend:2026-07-01-g17 splitStatements:true endDelimiter:;
INSERT INTO submission_criterion_grade (submission_id, criterion_id, points)
SELECT ls.id, gc.id, gc.max_points
FROM lesson_submission ls
         JOIN course_lesson cl ON ls.lesson_id = cl.id
         JOIN grading_criterion gc ON gc.lesson_id = cl.id AND gc.order_number = 2
         JOIN course c ON cl.course_id = c.id
WHERE ls.status = 'ACCEPTED'
  AND cl.order_number = 8
  AND cl.category = 'LESSON'
  AND c.for_lagging_students = FALSE AND c.is_introduction = FALSE
  AND NOT EXISTS (SELECT 1 FROM submission_criterion_grade scg
                  WHERE scg.submission_id = ls.id AND scg.criterion_id = gc.id);

--changeset Diplom_Backend:2026-07-01-g18 splitStatements:true endDelimiter:;
INSERT INTO submission_criterion_grade (submission_id, criterion_id, points)
SELECT ls.id, gc.id, gc.max_points
FROM lesson_submission ls
         JOIN course_lesson cl ON ls.lesson_id = cl.id
         JOIN grading_criterion gc ON gc.lesson_id = cl.id AND gc.order_number = 3
         JOIN course c ON cl.course_id = c.id
WHERE ls.status = 'ACCEPTED'
  AND cl.order_number = 8
  AND cl.category = 'LESSON'
  AND c.for_lagging_students = FALSE AND c.is_introduction = FALSE
  AND NOT EXISTS (SELECT 1 FROM submission_criterion_grade scg
                  WHERE scg.submission_id = ls.id AND scg.criterion_id = gc.id);


-- ─────────────────────────────────────────────────────────────────────────────
-- БЛОК 2Б: Вводные курсы — уроки 1,2,3 (по 10 б = 4+3+3)
-- ─────────────────────────────────────────────────────────────────────────────

--changeset Diplom_Backend:2026-07-01-g19 splitStatements:true endDelimiter:;
INSERT INTO submission_criterion_grade (submission_id, criterion_id, points)
SELECT ls.id, gc.id, gc.max_points
FROM lesson_submission ls
         JOIN course_lesson cl ON ls.lesson_id = cl.id
         JOIN grading_criterion gc ON gc.lesson_id = cl.id AND gc.order_number = 1
         JOIN course c ON cl.course_id = c.id
WHERE ls.status = 'ACCEPTED'
  AND cl.order_number IN (1, 2, 3)
  AND cl.category = 'LESSON'
  AND c.is_introduction = TRUE
  AND NOT EXISTS (SELECT 1 FROM submission_criterion_grade scg
                  WHERE scg.submission_id = ls.id AND scg.criterion_id = gc.id);

--changeset Diplom_Backend:2026-07-01-g20 splitStatements:true endDelimiter:;
INSERT INTO submission_criterion_grade (submission_id, criterion_id, points)
SELECT ls.id, gc.id, gc.max_points
FROM lesson_submission ls
         JOIN course_lesson cl ON ls.lesson_id = cl.id
         JOIN grading_criterion gc ON gc.lesson_id = cl.id AND gc.order_number = 2
         JOIN course c ON cl.course_id = c.id
WHERE ls.status = 'ACCEPTED'
  AND cl.order_number IN (1, 2, 3)
  AND cl.category = 'LESSON'
  AND c.is_introduction = TRUE
  AND NOT EXISTS (SELECT 1 FROM submission_criterion_grade scg
                  WHERE scg.submission_id = ls.id AND scg.criterion_id = gc.id);

--changeset Diplom_Backend:2026-07-01-g21 splitStatements:true endDelimiter:;
INSERT INTO submission_criterion_grade (submission_id, criterion_id, points)
SELECT ls.id, gc.id, gc.max_points
FROM lesson_submission ls
         JOIN course_lesson cl ON ls.lesson_id = cl.id
         JOIN grading_criterion gc ON gc.lesson_id = cl.id AND gc.order_number = 3
         JOIN course c ON cl.course_id = c.id
WHERE ls.status = 'ACCEPTED'
  AND cl.order_number IN (1, 2, 3)
  AND cl.category = 'LESSON'
  AND c.is_introduction = TRUE
  AND NOT EXISTS (SELECT 1 FROM submission_criterion_grade scg
                  WHERE scg.submission_id = ls.id AND scg.criterion_id = gc.id);


-- ─────────────────────────────────────────────────────────────────────────────
-- БЛОК 2В: Курсы для отстающих — уроки 1,2,3,4
-- ─────────────────────────────────────────────────────────────────────────────

--changeset Diplom_Backend:2026-07-01-g22 splitStatements:true endDelimiter:;
INSERT INTO submission_criterion_grade (submission_id, criterion_id, points)
SELECT ls.id, gc.id, gc.max_points
FROM lesson_submission ls
         JOIN course_lesson cl ON ls.lesson_id = cl.id
         JOIN grading_criterion gc ON gc.lesson_id = cl.id AND gc.order_number = 1
         JOIN course c ON cl.course_id = c.id
WHERE ls.status = 'ACCEPTED'
  AND cl.order_number IN (1, 2, 3, 4)
  AND cl.category = 'LESSON'
  AND c.for_lagging_students = TRUE
  AND NOT EXISTS (SELECT 1 FROM submission_criterion_grade scg
                  WHERE scg.submission_id = ls.id AND scg.criterion_id = gc.id);

--changeset Diplom_Backend:2026-07-01-g23 splitStatements:true endDelimiter:;
INSERT INTO submission_criterion_grade (submission_id, criterion_id, points)
SELECT ls.id, gc.id, gc.max_points
FROM lesson_submission ls
         JOIN course_lesson cl ON ls.lesson_id = cl.id
         JOIN grading_criterion gc ON gc.lesson_id = cl.id AND gc.order_number = 2
         JOIN course c ON cl.course_id = c.id
WHERE ls.status = 'ACCEPTED'
  AND cl.order_number IN (1, 2, 3, 4)
  AND cl.category = 'LESSON'
  AND c.for_lagging_students = TRUE
  AND NOT EXISTS (SELECT 1 FROM submission_criterion_grade scg
                  WHERE scg.submission_id = ls.id AND scg.criterion_id = gc.id);

--changeset Diplom_Backend:2026-07-01-g24 splitStatements:true endDelimiter:;
INSERT INTO submission_criterion_grade (submission_id, criterion_id, points)
SELECT ls.id, gc.id, gc.max_points
FROM lesson_submission ls
         JOIN course_lesson cl ON ls.lesson_id = cl.id
         JOIN grading_criterion gc ON gc.lesson_id = cl.id AND gc.order_number = 3
         JOIN course c ON cl.course_id = c.id
WHERE ls.status = 'ACCEPTED'
  AND cl.order_number IN (1, 2, 3, 4)
  AND cl.category = 'LESSON'
  AND c.for_lagging_students = TRUE
  AND NOT EXISTS (SELECT 1 FROM submission_criterion_grade scg
                  WHERE scg.submission_id = ls.id AND scg.criterion_id = gc.id);


-- =============================================================================
-- ═══ ЧАСТЬ 3: ИСТОРИЯ ПРОВЕРОК (submission_review_history) ═══════════════════
-- =============================================================================
-- Добавляем записи для ВСЕХ ACCEPTED lesson_submission, у которых ещё нет
-- записи в submission_review_history. Модераторы: ant.vasiliev, pol.kuptsova.
-- Логика: чётные submission.id → ant.vasiliev, нечётные → pol.kuptsova
-- (детерминированное распределение без хранимой процедуры)
-- =============================================================================

-- ─────────────────────────────────────────────────────────────────────────────
-- Рецензии для Сетевых технологий: уроки 2, 3, 4, 5, 7, 8
-- (для работ, у которых пока нет ни одной записи в истории)
-- ─────────────────────────────────────────────────────────────────────────────

-- Урок 2 (Анализ предметной области) — все ACCEPTED, ant.vasiliev
--changeset Diplom_Backend:2026-07-01-h01 splitStatements:true endDelimiter:;
INSERT INTO submission_review_history (submission_id, reviewer_account_id, entry_kind, status_after, score, reviewer_comment, created_at)
SELECT ls.id,
       (SELECT id FROM account WHERE nickname = 'ant.vasiliev' LIMIT 1),
       'MODERATOR',
       'ACCEPTED',
       ls.score,
       'Хороший анализ предметной области. Ключевые протоколы и технологии описаны корректно, источники подобраны по теме.',
       ls.submitted_at + INTERVAL '3 days'
FROM lesson_submission ls
         JOIN course_lesson cl ON ls.lesson_id = cl.id
         JOIN course c ON cl.course_id = c.id
WHERE ls.status = 'ACCEPTED'
  AND cl.order_number = 2
  AND c.name = 'Сетевые технологии'
  AND NOT EXISTS (SELECT 1 FROM submission_review_history srh WHERE srh.submission_id = ls.id);

-- Урок 3 (Анализ аналогов) — ACCEPTED, pol.kuptsova
--changeset Diplom_Backend:2026-07-01-h02 splitStatements:true endDelimiter:;
INSERT INTO submission_review_history (submission_id, reviewer_account_id, entry_kind, status_after, score, reviewer_comment, created_at)
SELECT ls.id,
       (SELECT id FROM account WHERE nickname = 'pol.kuptsova' LIMIT 1),
       'MODERATOR',
       'ACCEPTED',
       ls.score,
       'Аналоги рассмотрены, сравнительный анализ проведён. Уникальность проекта сформулирована обоснованно.',
       ls.submitted_at + INTERVAL '2 days'
FROM lesson_submission ls
         JOIN course_lesson cl ON ls.lesson_id = cl.id
         JOIN course c ON cl.course_id = c.id
WHERE ls.status = 'ACCEPTED'
  AND cl.order_number = 3
  AND c.name = 'Сетевые технологии'
  AND NOT EXISTS (SELECT 1 FROM submission_review_history srh WHERE srh.submission_id = ls.id);

-- Урок 4 (Проектирование) — ACCEPTED, ant.vasiliev
--changeset Diplom_Backend:2026-07-01-h03 splitStatements:true endDelimiter:;
INSERT INTO submission_review_history (submission_id, reviewer_account_id, entry_kind, status_after, score, reviewer_comment, created_at)
SELECT ls.id,
       (SELECT id FROM account WHERE nickname = 'ant.vasiliev' LIMIT 1),
       'MODERATOR',
       'ACCEPTED',
       ls.score,
       'Архитектура проекта проработана, технологический стек обоснован. Схемы и диаграммы наглядны.',
       ls.submitted_at + INTERVAL '4 days'
FROM lesson_submission ls
         JOIN course_lesson cl ON ls.lesson_id = cl.id
         JOIN course c ON cl.course_id = c.id
WHERE ls.status = 'ACCEPTED'
  AND cl.order_number = 4
  AND c.name = 'Сетевые технологии'
  AND NOT EXISTS (SELECT 1 FROM submission_review_history srh
                  WHERE srh.submission_id = ls.id AND srh.entry_kind = 'MODERATOR' AND srh.status_after = 'ACCEPTED');

-- Урок 5 (Разработка) — ACCEPTED, pol.kuptsova
--changeset Diplom_Backend:2026-07-01-h04 splitStatements:true endDelimiter:;
INSERT INTO submission_review_history (submission_id, reviewer_account_id, entry_kind, status_after, score, reviewer_comment, created_at)
SELECT ls.id,
       (SELECT id FROM account WHERE nickname = 'pol.kuptsova' LIMIT 1),
       'MODERATOR',
       'ACCEPTED',
       ls.score,
       'Прототип реализован, базовая функциональность работает. Код структурирован, задокументирован.',
       ls.submitted_at + INTERVAL '3 days'
FROM lesson_submission ls
         JOIN course_lesson cl ON ls.lesson_id = cl.id
         JOIN course c ON cl.course_id = c.id
WHERE ls.status = 'ACCEPTED'
  AND cl.order_number = 5
  AND c.name = 'Сетевые технологии'
  AND NOT EXISTS (SELECT 1 FROM submission_review_history srh
                  WHERE srh.submission_id = ls.id AND srh.entry_kind = 'MODERATOR' AND srh.status_after = 'ACCEPTED');

-- Урок 7 (Пояснительная записка) — ACCEPTED, ant.vasiliev
--changeset Diplom_Backend:2026-07-01-h05 splitStatements:true endDelimiter:;
INSERT INTO submission_review_history (submission_id, reviewer_account_id, entry_kind, status_after, score, reviewer_comment, created_at)
SELECT ls.id,
       (SELECT id FROM account WHERE nickname = 'ant.vasiliev' LIMIT 1),
       'MODERATOR',
       'ACCEPTED',
       ls.score,
       'Пояснительная записка оформлена по требованиям ГОСТ. Структура документа полная, текст грамотный.',
       ls.submitted_at + INTERVAL '5 days'
FROM lesson_submission ls
         JOIN course_lesson cl ON ls.lesson_id = cl.id
         JOIN course c ON cl.course_id = c.id
WHERE ls.status = 'ACCEPTED'
  AND cl.order_number = 7
  AND c.name = 'Сетевые технологии'
  AND NOT EXISTS (SELECT 1 FROM submission_review_history srh WHERE srh.submission_id = ls.id);

-- Урок 8 (Презентация) — ACCEPTED, pol.kuptsova
--changeset Diplom_Backend:2026-07-01-h06 splitStatements:true endDelimiter:;
INSERT INTO submission_review_history (submission_id, reviewer_account_id, entry_kind, status_after, score, reviewer_comment, created_at)
SELECT ls.id,
       (SELECT id FROM account WHERE nickname = 'pol.kuptsova' LIMIT 1),
       'MODERATOR',
       'ACCEPTED',
       ls.score,
       'Презентация логично структурирована, визуально оформлена в едином стиле. Результаты продемонстрированы наглядно.',
       ls.submitted_at + INTERVAL '3 days'
FROM lesson_submission ls
         JOIN course_lesson cl ON ls.lesson_id = cl.id
         JOIN course c ON cl.course_id = c.id
WHERE ls.status = 'ACCEPTED'
  AND cl.order_number = 8
  AND c.name = 'Сетевые технологии'
  AND NOT EXISTS (SELECT 1 FROM submission_review_history srh WHERE srh.submission_id = ls.id);


-- ─────────────────────────────────────────────────────────────────────────────
-- Рецензии для «Введение в разработку ПО»: уроки 1, 2, 3 (только ACCEPTED)
-- ─────────────────────────────────────────────────────────────────────────────

--changeset Diplom_Backend:2026-07-01-h07 splitStatements:true endDelimiter:;
INSERT INTO submission_review_history (submission_id, reviewer_account_id, entry_kind, status_after, score, reviewer_comment, created_at)
SELECT ls.id,
       CASE WHEN ls.id % 2 = 0
                THEN (SELECT id FROM account WHERE nickname = 'ant.vasiliev' LIMIT 1)
            ELSE (SELECT id FROM account WHERE nickname = 'pol.kuptsova' LIMIT 1)
           END,
       'MODERATOR',
       'ACCEPTED',
       ls.score,
       CASE cl.order_number
           WHEN 1 THEN 'Задание выполнено. Методология Agile раскрыта верно, приведены собственные примеры применения.'
           WHEN 2 THEN 'Репозиторий создан, README оформлен. Первый коммит выполнен корректно.'
           WHEN 3 THEN 'Прототип в Figma загружен. Экраны соответствуют описанному функционалу.'
           END,
       ls.submitted_at + INTERVAL '2 days'
FROM lesson_submission ls
         JOIN course_lesson cl ON ls.lesson_id = cl.id
         JOIN course c ON cl.course_id = c.id
WHERE ls.status = 'ACCEPTED'
  AND cl.order_number IN (1, 2, 3)
  AND c.name = 'Введение в разработку программного обеспечения'
  AND NOT EXISTS (SELECT 1 FROM submission_review_history srh WHERE srh.submission_id = ls.id);


-- ─────────────────────────────────────────────────────────────────────────────
-- Рецензии для «Основы проектной деятельности» (вводный курс): уроки 1, 2, 3
-- ─────────────────────────────────────────────────────────────────────────────

--changeset Diplom_Backend:2026-07-01-h08 splitStatements:true endDelimiter:;
INSERT INTO submission_review_history (submission_id, reviewer_account_id, entry_kind, status_after, score, reviewer_comment, created_at)
SELECT ls.id,
       CASE WHEN ls.id % 2 = 0
                THEN (SELECT id FROM account WHERE nickname = 'ant.vasiliev' LIMIT 1)
            ELSE (SELECT id FROM account WHERE nickname = 'pol.kuptsova' LIMIT 1)
           END,
       'MODERATOR',
       'ACCEPTED',
       ls.score,
       CASE cl.order_number
           WHEN 1 THEN 'Понимание материала продемонстрировано. Ответ полный, сформулированы собственные выводы.'
           WHEN 2 THEN 'Задание выполнено в полном объёме. Приведены конкретные примеры из практики.'
           WHEN 3 THEN 'Работа оформлена аккуратно, задание выполнено самостоятельно и в срок.'
           END,
       ls.submitted_at + INTERVAL '2 days'
FROM lesson_submission ls
         JOIN course_lesson cl ON ls.lesson_id = cl.id
         JOIN course c ON cl.course_id = c.id
WHERE ls.status = 'ACCEPTED'
  AND cl.order_number IN (1, 2, 3)
  AND c.name = 'Основы проектной деятельности'
  AND NOT EXISTS (SELECT 1 FROM submission_review_history srh WHERE srh.submission_id = ls.id);


-- ─────────────────────────────────────────────────────────────────────────────
-- Рецензии для курсов «для отстающих»: все ACCEPTED уроки 1-4
-- ─────────────────────────────────────────────────────────────────────────────

--changeset Diplom_Backend:2026-07-01-h09 splitStatements:true endDelimiter:;
INSERT INTO submission_review_history (submission_id, reviewer_account_id, entry_kind, status_after, score, reviewer_comment, created_at)
SELECT ls.id,
       CASE WHEN ls.id % 2 = 0
                THEN (SELECT id FROM account WHERE nickname = 'ant.vasiliev' LIMIT 1)
            ELSE (SELECT id FROM account WHERE nickname = 'pol.kuptsova' LIMIT 1)
           END,
       'MODERATOR',
       'ACCEPTED',
       ls.score,
       CASE cl.order_number
           WHEN 1 THEN 'Задание урока выполнено. Базовые понятия усвоены, применение корректно.'
           WHEN 2 THEN 'Работа сдана в полном объёме. Прогресс виден, продолжайте в том же темпе.'
           WHEN 3 THEN 'Задание выполнено качественно. Грамотность и структура на хорошем уровне.'
           WHEN 4 THEN 'Завершающее задание выполнено. Код/результат структурирован и прокомментирован.'
           END,
       ls.submitted_at + INTERVAL '3 days'
FROM lesson_submission ls
         JOIN course_lesson cl ON ls.lesson_id = cl.id
         JOIN course c ON cl.course_id = c.id
WHERE ls.status = 'ACCEPTED'
  AND cl.order_number IN (1, 2, 3, 4)
  AND cl.category = 'LESSON'
  AND c.for_lagging_students = TRUE
  AND NOT EXISTS (SELECT 1 FROM submission_review_history srh WHERE srh.submission_id = ls.id);


-- ─────────────────────────────────────────────────────────────────────────────
-- Рецензии для NEEDS_REVISION работ (только первичная рецензия модератора)
-- Сетевые технологии: уроки 4 (mar.egor уже добавлен в 06-12, пропускаем)
-- Остальные NEEDS_REVISION по всем курсам
-- ─────────────────────────────────────────────────────────────────────────────

--changeset Diplom_Backend:2026-07-01-h10 splitStatements:true endDelimiter:;
INSERT INTO submission_review_history (submission_id, reviewer_account_id, entry_kind, status_after, score, reviewer_comment, created_at)
SELECT ls.id,
       CASE WHEN ls.id % 2 = 0
                THEN (SELECT id FROM account WHERE nickname = 'ant.vasiliev' LIMIT 1)
            ELSE (SELECT id FROM account WHERE nickname = 'pol.kuptsova' LIMIT 1)
           END,
       'MODERATOR',
       'NEEDS_REVISION',
       NULL,
       'Работа требует доработки. Не все пункты задания выполнены — необходимо дополнить материал и устранить выявленные замечания.',
       ls.submitted_at + INTERVAL '2 days'
FROM lesson_submission ls
         JOIN course_lesson cl ON ls.lesson_id = cl.id
WHERE ls.status = 'NEEDS_REVISION'
  AND cl.category = 'LESSON'
  AND NOT EXISTS (SELECT 1 FROM submission_review_history srh
                  WHERE srh.submission_id = ls.id AND srh.entry_kind = 'MODERATOR');


-- =============================================================================
-- ═══ ЧАСТЬ 4: HEARING_REVIEW ДЛЯ TOPIC_APPROVAL И INTERMEDIATE ═══════════════
-- =============================================================================
-- Для ACCEPTED слушаний первых двух этапов добавляем рецензии модераторов,
-- если их ещё нет.
-- =============================================================================

-- TOPIC_APPROVAL — ACCEPTED: рецензия-одобрение
--changeset Diplom_Backend:2026-07-01-r01 splitStatements:true endDelimiter:;
INSERT INTO hearing_review (submission_id, moderator_id, comment, grade, submission_version, reviewed_at)
SELECT hs.id,
       CASE WHEN hs.id % 2 = 0
                THEN (SELECT id FROM account WHERE nickname = 'ant.vasiliev' LIMIT 1)
            ELSE (SELECT id FROM account WHERE nickname = 'pol.kuptsova' LIMIT 1)
           END,
       'Тема согласована. Формулировка актуальна, цель и задачи чётко обозначены. Проект реализуем в рамках учебного года.',
       10,
       hs.current_version,
       hs.submitted_at + INTERVAL '3 days'
FROM hearing_submission hs
         JOIN course_lesson cl ON hs.lesson_id = cl.id
WHERE hs.status = 'ACCEPTED'
  AND cl.hearing_stage = 'TOPIC_APPROVAL'
  AND NOT EXISTS (SELECT 1 FROM hearing_review hr WHERE hr.submission_id = hs.id);

-- TOPIC_APPROVAL — NEEDS_REVISION: рецензия с замечаниями
--changeset Diplom_Backend:2026-07-01-r02 splitStatements:true endDelimiter:;
INSERT INTO hearing_review (submission_id, moderator_id, comment, grade, submission_version, reviewed_at)
SELECT hs.id,
       CASE WHEN hs.id % 2 = 0
                THEN (SELECT id FROM account WHERE nickname = 'ant.vasiliev' LIMIT 1)
            ELSE (SELECT id FROM account WHERE nickname = 'pol.kuptsova' LIMIT 1)
           END,
       'Тема требует уточнения. Необходимо конкретизировать цель проекта и обосновать его актуальность. Укажите предполагаемый результат и технологии реализации.',
       NULL,
       hs.current_version,
       hs.submitted_at + INTERVAL '2 days'
FROM hearing_submission hs
         JOIN course_lesson cl ON hs.lesson_id = cl.id
WHERE hs.status = 'NEEDS_REVISION'
  AND cl.hearing_stage = 'TOPIC_APPROVAL'
  AND NOT EXISTS (SELECT 1 FROM hearing_review hr WHERE hr.submission_id = hs.id);

-- INTERMEDIATE — ACCEPTED: рецензия промежуточного показа
--changeset Diplom_Backend:2026-07-01-r03 splitStatements:true endDelimiter:;
INSERT INTO hearing_review (submission_id, moderator_id, comment, grade, submission_version, reviewed_at)
SELECT hs.id,
       CASE WHEN hs.id % 2 = 0
                THEN (SELECT id FROM account WHERE nickname = 'ant.vasiliev' LIMIT 1)
            ELSE (SELECT id FROM account WHERE nickname = 'pol.kuptsova' LIMIT 1)
           END,
       'Промежуточный показ пройден. Прогресс соответствует плану: ключевые этапы выполнены, прототип демонстрирует основную функциональность. Команда работает слаженно.',
       20,
       hs.current_version,
       hs.submitted_at + INTERVAL '4 days'
FROM hearing_submission hs
         JOIN course_lesson cl ON hs.lesson_id = cl.id
WHERE hs.status = 'ACCEPTED'
  AND cl.hearing_stage = 'INTERMEDIATE'
  AND NOT EXISTS (SELECT 1 FROM hearing_review hr WHERE hr.submission_id = hs.id);

-- INTERMEDIATE — NEEDS_REVISION
--changeset Diplom_Backend:2026-07-01-r04 splitStatements:true endDelimiter:;
INSERT INTO hearing_review (submission_id, moderator_id, comment, grade, submission_version, reviewed_at)
SELECT hs.id,
       CASE WHEN hs.id % 2 = 0
                THEN (SELECT id FROM account WHERE nickname = 'ant.vasiliev' LIMIT 1)
            ELSE (SELECT id FROM account WHERE nickname = 'pol.kuptsova' LIMIT 1)
           END,
       'Промежуточный показ не принят. Выполнено менее 50% планируемого объёма. Необходимо ускорить реализацию: подготовить работающий прототип и дополнить пояснительную записку.',
       NULL,
       hs.current_version,
       hs.submitted_at + INTERVAL '3 days'
FROM hearing_submission hs
         JOIN course_lesson cl ON hs.lesson_id = cl.id
WHERE hs.status = 'NEEDS_REVISION'
  AND cl.hearing_stage = 'INTERMEDIATE'
  AND NOT EXISTS (SELECT 1 FROM hearing_review hr WHERE hr.submission_id = hs.id);

-- FINAL — ACCEPTED: рецензия финального показа
--changeset Diplom_Backend:2026-07-01-r05 splitStatements:true endDelimiter:;
INSERT INTO hearing_review (submission_id, moderator_id, comment, grade, submission_version, reviewed_at)
SELECT hs.id,
       CASE WHEN hs.id % 2 = 0
                THEN (SELECT id FROM account WHERE nickname = 'ant.vasiliev' LIMIT 1)
            ELSE (SELECT id FROM account WHERE nickname = 'pol.kuptsova' LIMIT 1)
           END,
       'Финальный показ прошёл успешно. Проект завершён в полном объёме. Пояснительная записка и презентация соответствуют требованиям. Команда уверенно владеет материалом.',
       30,
       hs.current_version,
       hs.submitted_at + INTERVAL '5 days'
FROM hearing_submission hs
         JOIN course_lesson cl ON hs.lesson_id = cl.id
WHERE hs.status = 'ACCEPTED'
  AND cl.hearing_stage = 'FINAL'
  AND NOT EXISTS (SELECT 1 FROM hearing_review hr WHERE hr.submission_id = hs.id);

-- FINAL — NEEDS_REVISION
--changeset Diplom_Backend:2026-07-01-r06 splitStatements:true endDelimiter:;
INSERT INTO hearing_review (submission_id, moderator_id, comment, grade, submission_version, reviewed_at)
SELECT hs.id,
       CASE WHEN hs.id % 2 = 0
                THEN (SELECT id FROM account WHERE nickname = 'ant.vasiliev' LIMIT 1)
            ELSE (SELECT id FROM account WHERE nickname = 'pol.kuptsova' LIMIT 1)
           END,
       'Финальный показ требует доработки. Необходимо устранить замечания: дополнить раздел «Результаты» в пояснительной записке, улучшить структуру презентации и подготовить более полную демонстрацию.',
       NULL,
       hs.current_version,
       hs.submitted_at + INTERVAL '3 days'
FROM hearing_submission hs
         JOIN course_lesson cl ON hs.lesson_id = cl.id
WHERE hs.status = 'NEEDS_REVISION'
  AND cl.hearing_stage = 'FINAL'
  AND NOT EXISTS (SELECT 1 FROM hearing_review hr WHERE hr.submission_id = hs.id);