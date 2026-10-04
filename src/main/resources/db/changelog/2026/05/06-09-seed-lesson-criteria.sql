--liquibase formatted sql

-- =============================================================================
-- Критерии оценивания для уроков курсов.
-- Покрывает:
--   1. Уроки целевых ИТ-курсов (order 2-5, 7, 8) — единая структура критериев
--   2. Уроки вводных курсов («Введение в разработку ПО», «Основы проектной деятельности»)
--   3. Уроки курсов для отстающих («Индивидуальная проектная работа», «Основы программирования для отстающих»)
--   4. CONFERENCE_DEFENSE критерии для уроков, созданных ПОСЛЕ применения 04-04
--      (покрывает курсы из 06-04 и любые будущие курсы с этим этапом)
-- Все INSERT идемпотентны через NOT EXISTS по (lesson_id, order_number).
-- =============================================================================

-- =============================================================================
-- БЛОК 1. Критерии для уроков целевых ИТ-курсов
-- Структура одинакова для всех курсов: уроки 2,3,4 (10б), 5 (15б), 7 (10б), 8 (15б)
-- Критерии по каждому уроку: 3 критерия, сумма = max_score урока
-- =============================================================================

-- ── Урок 2: Анализ предметной области (10 баллов = 3+4+3) ────────────────────

--changeset Diplom_Backend:2026-06-09-01 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 1, 'Полнота и глубина анализа', 'Насколько подробно изучена предметная область: источники, термины, ключевые понятия.', 3
FROM course_lesson cl
WHERE cl.order_number = 2 AND cl.category = 'LESSON'
  AND EXISTS (SELECT 1 FROM course c WHERE c.id = cl.course_id AND c.for_lagging_students = FALSE AND c.is_introduction = FALSE)
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 1);

--changeset Diplom_Backend:2026-06-09-02 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 2, 'Качество оформления', 'Структурированность материала, наличие ссылок на источники, грамотность изложения.', 4
FROM course_lesson cl
WHERE cl.order_number = 2 AND cl.category = 'LESSON'
  AND EXISTS (SELECT 1 FROM course c WHERE c.id = cl.course_id AND c.for_lagging_students = FALSE AND c.is_introduction = FALSE)
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 2);

--changeset Diplom_Backend:2026-06-09-03 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 3, 'Связь с проектом', 'Насколько анализ обоснован применительно к теме проекта, есть ли выводы.', 3
FROM course_lesson cl
WHERE cl.order_number = 2 AND cl.category = 'LESSON'
  AND EXISTS (SELECT 1 FROM course c WHERE c.id = cl.course_id AND c.for_lagging_students = FALSE AND c.is_introduction = FALSE)
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 3);

-- ── Урок 3: Анализ аналогов и конкурентов (10 баллов = 3+4+3) ────────────────

--changeset Diplom_Backend:2026-06-09-04 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 1, 'Количество и качество аналогов', 'Рассмотрено не менее 3 аналогов с описанием их функций и характеристик.', 3
FROM course_lesson cl
WHERE cl.order_number = 3 AND cl.category = 'LESSON'
  AND EXISTS (SELECT 1 FROM course c WHERE c.id = cl.course_id AND c.for_lagging_students = FALSE AND c.is_introduction = FALSE)
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 1);

--changeset Diplom_Backend:2026-06-09-05 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 2, 'Сравнительный анализ', 'Проведено сравнение аналогов по выбранным критериям, представлена сравнительная таблица.', 4
FROM course_lesson cl
WHERE cl.order_number = 3 AND cl.category = 'LESSON'
  AND EXISTS (SELECT 1 FROM course c WHERE c.id = cl.course_id AND c.for_lagging_students = FALSE AND c.is_introduction = FALSE)
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 2);

--changeset Diplom_Backend:2026-06-09-06 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 3, 'Обоснование уникальности проекта', 'Сформулировано отличие своего проекта от аналогов, описаны преимущества.', 3
FROM course_lesson cl
WHERE cl.order_number = 3 AND cl.category = 'LESSON'
  AND EXISTS (SELECT 1 FROM course c WHERE c.id = cl.course_id AND c.for_lagging_students = FALSE AND c.is_introduction = FALSE)
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 3);

-- ── Урок 4: Проектирование ИТ-проекта (10 баллов = 4+3+3) ────────────────────

--changeset Diplom_Backend:2026-06-09-07 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 1, 'Полнота архитектурного описания', 'Наличие схем, диаграмм, технического задания или макетов — в зависимости от типа проекта.', 4
FROM course_lesson cl
WHERE cl.order_number = 4 AND cl.category = 'LESSON'
  AND EXISTS (SELECT 1 FROM course c WHERE c.id = cl.course_id AND c.for_lagging_students = FALSE AND c.is_introduction = FALSE)
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 1);

--changeset Diplom_Backend:2026-06-09-08 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 2, 'Обоснование технологических решений', 'Обоснован выбор инструментов, языка программирования, платформы или оборудования.', 3
FROM course_lesson cl
WHERE cl.order_number = 4 AND cl.category = 'LESSON'
  AND EXISTS (SELECT 1 FROM course c WHERE c.id = cl.course_id AND c.for_lagging_students = FALSE AND c.is_introduction = FALSE)
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 2);

--changeset Diplom_Backend:2026-06-09-09 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 3, 'Реализуемость плана', 'Предложенное решение реалистично в рамках имеющихся ресурсов и сроков.', 3
FROM course_lesson cl
WHERE cl.order_number = 4 AND cl.category = 'LESSON'
  AND EXISTS (SELECT 1 FROM course c WHERE c.id = cl.course_id AND c.for_lagging_students = FALSE AND c.is_introduction = FALSE)
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 3);

-- ── Урок 5: Разработка ИТ-проекта (15 баллов = 5+5+5) ────────────────────────

--changeset Diplom_Backend:2026-06-09-10 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 1, 'Работоспособность прототипа', 'Прототип запускается, базовый функционал работает без критических ошибок.', 5
FROM course_lesson cl
WHERE cl.order_number = 5 AND cl.category = 'LESSON'
  AND EXISTS (SELECT 1 FROM course c WHERE c.id = cl.course_id AND c.for_lagging_students = FALSE AND c.is_introduction = FALSE)
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 1);

--changeset Diplom_Backend:2026-06-09-11 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 2, 'Соответствие проектному заданию', 'Реализованная функциональность соответствует описанию из урока 4.', 5
FROM course_lesson cl
WHERE cl.order_number = 5 AND cl.category = 'LESSON'
  AND EXISTS (SELECT 1 FROM course c WHERE c.id = cl.course_id AND c.for_lagging_students = FALSE AND c.is_introduction = FALSE)
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 2);

--changeset Diplom_Backend:2026-06-09-12 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 3, 'Качество кода/технического решения', 'Читаемость кода, структура проекта, наличие комментариев; для аппаратных проектов — качество сборки.', 5
FROM course_lesson cl
WHERE cl.order_number = 5 AND cl.category = 'LESSON'
  AND EXISTS (SELECT 1 FROM course c WHERE c.id = cl.course_id AND c.for_lagging_students = FALSE AND c.is_introduction = FALSE)
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 3);

-- ── Урок 7: Оформление пояснительной записки (10 баллов = 3+4+3) ─────────────

--changeset Diplom_Backend:2026-06-09-13 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 1, 'Структура документа', 'Наличие всех обязательных разделов: титул, введение, основная часть, заключение, список источников.', 3
FROM course_lesson cl
WHERE cl.order_number = 7 AND cl.category = 'LESSON'
  AND EXISTS (SELECT 1 FROM course c WHERE c.id = cl.course_id AND c.for_lagging_students = FALSE AND c.is_introduction = FALSE)
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 1);

--changeset Diplom_Backend:2026-06-09-14 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 2, 'Соответствие оформления требованиям', 'Шрифт, поля, нумерация страниц, оформление рисунков и таблиц соответствуют шаблону МосПолитех.', 4
FROM course_lesson cl
WHERE cl.order_number = 7 AND cl.category = 'LESSON'
  AND EXISTS (SELECT 1 FROM course c WHERE c.id = cl.course_id AND c.for_lagging_students = FALSE AND c.is_introduction = FALSE)
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 2);

--changeset Diplom_Backend:2026-06-09-15 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 3, 'Содержательность текста', 'Текст грамотный, логично изложен, содержит конкретные технические описания и выводы.', 3
FROM course_lesson cl
WHERE cl.order_number = 7 AND cl.category = 'LESSON'
  AND EXISTS (SELECT 1 FROM course c WHERE c.id = cl.course_id AND c.for_lagging_students = FALSE AND c.is_introduction = FALSE)
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 3);

-- ── Урок 8: Презентация ИТ-проекта (15 баллов = 5+5+5) ───────────────────────

--changeset Diplom_Backend:2026-06-09-16 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 1, 'Структура и содержание презентации', 'Логичная последовательность слайдов: постановка задачи, решение, результаты, выводы. Не менее 8 слайдов.', 5
FROM course_lesson cl
WHERE cl.order_number = 8 AND cl.category = 'LESSON'
  AND EXISTS (SELECT 1 FROM course c WHERE c.id = cl.course_id AND c.for_lagging_students = FALSE AND c.is_introduction = FALSE)
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 1);

--changeset Diplom_Backend:2026-06-09-17 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 2, 'Визуальное оформление', 'Единый стиль, читаемые шрифты, качественные иллюстрации, соответствие брендбуку МосПолитех.', 5
FROM course_lesson cl
WHERE cl.order_number = 8 AND cl.category = 'LESSON'
  AND EXISTS (SELECT 1 FROM course c WHERE c.id = cl.course_id AND c.for_lagging_students = FALSE AND c.is_introduction = FALSE)
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 2);

--changeset Diplom_Backend:2026-06-09-18 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 3, 'Демонстрация результатов', 'Наличие скриншотов, видео или демонстрации работающего продукта/прототипа.', 5
FROM course_lesson cl
WHERE cl.order_number = 8 AND cl.category = 'LESSON'
  AND EXISTS (SELECT 1 FROM course c WHERE c.id = cl.course_id AND c.for_lagging_students = FALSE AND c.is_introduction = FALSE)
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 3);

-- =============================================================================
-- БЛОК 2. Критерии для уроков вводных курсов
-- «Введение в разработку ПО» и «Основы проектной деятельности»
-- =============================================================================

-- ── Вводные курсы: урок 1 (TEXT, 10б = 4+3+3) ────────────────────────────────

--changeset Diplom_Backend:2026-06-09-19 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 1, 'Понимание материала', 'Ответ демонстрирует понимание основных понятий, изложенных в лекции.', 4
FROM course_lesson cl
WHERE cl.order_number = 1 AND cl.category = 'LESSON'
  AND EXISTS (SELECT 1 FROM course c WHERE c.id = cl.course_id AND c.is_introduction = TRUE)
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 1);

--changeset Diplom_Backend:2026-06-09-20 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 2, 'Самостоятельность и оригинальность', 'Приведены собственные примеры, сформулирован личный вывод.', 3
FROM course_lesson cl
WHERE cl.order_number = 1 AND cl.category = 'LESSON'
  AND EXISTS (SELECT 1 FROM course c WHERE c.id = cl.course_id AND c.is_introduction = TRUE)
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 2);

--changeset Diplom_Backend:2026-06-09-21 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 3, 'Полнота ответа', 'Ответ покрывает все вопросы задания, не менее 3–5 предложений.', 3
FROM course_lesson cl
WHERE cl.order_number = 1 AND cl.category = 'LESSON'
  AND EXISTS (SELECT 1 FROM course c WHERE c.id = cl.course_id AND c.is_introduction = TRUE)
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 3);

-- ── Вводные курсы: урок 2 (TEXT, 10б = 4+3+3) ────────────────────────────────

--changeset Diplom_Backend:2026-06-09-22 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 1, 'Полнота выполнения задания', 'Все пункты задания выполнены: изучены материалы, дан развёрнутый ответ.', 4
FROM course_lesson cl
WHERE cl.order_number = 2 AND cl.category = 'LESSON'
  AND EXISTS (SELECT 1 FROM course c WHERE c.id = cl.course_id AND c.is_introduction = TRUE)
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 1);

--changeset Diplom_Backend:2026-06-09-23 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 2, 'Практическое применение', 'Приведён конкретный практический пример или выполнено практическое задание.', 3
FROM course_lesson cl
WHERE cl.order_number = 2 AND cl.category = 'LESSON'
  AND EXISTS (SELECT 1 FROM course c WHERE c.id = cl.course_id AND c.is_introduction = TRUE)
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 2);

--changeset Diplom_Backend:2026-06-09-24 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 3, 'Грамотность и оформление', 'Текст написан грамотно, структурирован, без орфографических ошибок.', 3
FROM course_lesson cl
WHERE cl.order_number = 2 AND cl.category = 'LESSON'
  AND EXISTS (SELECT 1 FROM course c WHERE c.id = cl.course_id AND c.is_introduction = TRUE)
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 3);

-- ── Вводные курсы: урок 3 (10б = 4+3+3) ─────────────────────────────────────

--changeset Diplom_Backend:2026-06-09-25 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 1, 'Полнота выполнения задания', 'Задание выполнено в полном объёме, приложены все требуемые материалы.', 4
FROM course_lesson cl
WHERE cl.order_number = 3 AND cl.category = 'LESSON'
  AND EXISTS (SELECT 1 FROM course c WHERE c.id = cl.course_id AND c.is_introduction = TRUE)
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 1);

--changeset Diplom_Backend:2026-06-09-26 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 2, 'Самостоятельность', 'Работа выполнена самостоятельно, видно авторское мышление.', 3
FROM course_lesson cl
WHERE cl.order_number = 3 AND cl.category = 'LESSON'
  AND EXISTS (SELECT 1 FROM course c WHERE c.id = cl.course_id AND c.is_introduction = TRUE)
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 2);

--changeset Diplom_Backend:2026-06-09-27 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 3, 'Качество оформления', 'Работа аккуратно оформлена, соответствует требованиям задания.', 3
FROM course_lesson cl
WHERE cl.order_number = 3 AND cl.category = 'LESSON'
  AND EXISTS (SELECT 1 FROM course c WHERE c.id = cl.course_id AND c.is_introduction = TRUE)
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 3);

-- ── Вводный курс «Введение в разработку ПО»: урок 3 — FILE (15б = 5+5+5) ─────

--changeset Diplom_Backend:2026-06-09-28 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 1, 'Наличие прототипа', 'Файл с прототипом загружен, прототип открывается и содержит хотя бы один экран.', 5
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
WHERE c.name = 'Введение в разработку программного обеспечения'
  AND cl.order_number = 3 AND cl.category = 'LESSON'
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 1);

--changeset Diplom_Backend:2026-06-09-29 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 2, 'Соответствие теме проекта', 'Прототип отражает тему будущего проекта, содержит основные элементы интерфейса.', 5
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
WHERE c.name = 'Введение в разработку программного обеспечения'
  AND cl.order_number = 3 AND cl.category = 'LESSON'
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 2);

--changeset Diplom_Backend:2026-06-09-30 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 3, 'Качество дизайна', 'Прототип аккуратно оформлен: читаемые шрифты, логичное расположение элементов.', 5
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
WHERE c.name = 'Введение в разработку программного обеспечения'
  AND cl.order_number = 3 AND cl.category = 'LESSON'
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 3);

-- ── Вводный курс «Основы проектной деятельности»: урок 4 (10б = 4+3+3) ───────

--changeset Diplom_Backend:2026-06-09-31 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 1, 'Выбор темы', 'Тема проекта выбрана и кратко описана: направление, идея, предполагаемый результат.', 4
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
WHERE c.name = 'Основы проектной деятельности' AND cl.order_number = 4 AND cl.category = 'LESSON'
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 1);

--changeset Diplom_Backend:2026-06-09-32 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 2, 'Состав группы', 'Определён состав проектной группы или обосновано самостоятельное выполнение.', 3
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
WHERE c.name = 'Основы проектной деятельности' AND cl.order_number = 4 AND cl.category = 'LESSON'
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 2);

--changeset Diplom_Backend:2026-06-09-33 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 3, 'Обоснование выбора', 'Объяснено, почему выбрана именно эта тема, какую проблему она решает.', 3
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
WHERE c.name = 'Основы проектной деятельности' AND cl.order_number = 4 AND cl.category = 'LESSON'
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 3);

-- =============================================================================
-- БЛОК 3. Критерии для уроков курсов для отстающих
-- «Индивидуальная проектная работа» и «Основы программирования для отстающих»
-- =============================================================================

-- ── Уроки 2,3 в lagging-курсах (TEXT/TEXT_AND_FILE, 10б = 4+3+3) ─────────────

--changeset Diplom_Backend:2026-06-09-34 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 1, 'Полнота выполнения', 'Задание выполнено полностью, представлен развёрнутый ответ.', 4
FROM course_lesson cl
WHERE cl.order_number IN (2, 3) AND cl.category = 'LESSON'
  AND EXISTS (SELECT 1 FROM course c WHERE c.id = cl.course_id AND c.for_lagging_students = TRUE)
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 1);

--changeset Diplom_Backend:2026-06-09-35 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 2, 'Самостоятельность', 'Работа выполнена самостоятельно, видно понимание материала.', 3
FROM course_lesson cl
WHERE cl.order_number IN (2, 3) AND cl.category = 'LESSON'
  AND EXISTS (SELECT 1 FROM course c WHERE c.id = cl.course_id AND c.for_lagging_students = TRUE)
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 2);

--changeset Diplom_Backend:2026-06-09-36 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 3, 'Качество оформления', 'Работа аккуратно оформлена и понятно изложена.', 3
FROM course_lesson cl
WHERE cl.order_number IN (2, 3) AND cl.category = 'LESSON'
  AND EXISTS (SELECT 1 FROM course c WHERE c.id = cl.course_id AND c.for_lagging_students = TRUE)
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 3);

-- ── Урок 4: Разработка (FILE, 20б = 7+7+6) ───────────────────────────────────

--changeset Diplom_Backend:2026-06-09-37 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 1, 'Работоспособность результата', 'Загруженный файл содержит работающий прототип или реализацию, которая запускается.', 7
FROM course_lesson cl
WHERE cl.order_number = 4 AND cl.category = 'LESSON'
  AND EXISTS (SELECT 1 FROM course c WHERE c.id = cl.course_id AND c.for_lagging_students = TRUE)
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 1);

--changeset Diplom_Backend:2026-06-09-38 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 2, 'Соответствие заданию', 'Реализованная функциональность соответствует описанию задания.', 7
FROM course_lesson cl
WHERE cl.order_number = 4 AND cl.category = 'LESSON'
  AND EXISTS (SELECT 1 FROM course c WHERE c.id = cl.course_id AND c.for_lagging_students = TRUE)
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 2);

--changeset Diplom_Backend:2026-06-09-39 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 3, 'Качество выполнения', 'Код/результат структурирован, снабжён комментариями или инструкцией.', 6
FROM course_lesson cl
WHERE cl.order_number = 4 AND cl.category = 'LESSON'
  AND EXISTS (SELECT 1 FROM course c WHERE c.id = cl.course_id AND c.for_lagging_students = TRUE)
  AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 3);

-- =============================================================================
-- БЛОК 4. CONFERENCE_DEFENSE — критерии для уроков, созданных ПОСЛЕ 04-04
-- Та же логика что в 04-04, но покрывает уроки без критериев
-- =============================================================================

--changeset Diplom_Backend:2026-06-09-40 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 1, 'Актуальность выбранной проблемы',
       'Актуальность и значимость выбранной проблемы, наличие вариантов эффективного решения (обзор литературы и существующих подходов).', 3
FROM course_lesson cl WHERE cl.hearing_stage = 'CONFERENCE_DEFENSE'
                        AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 1);

--changeset Diplom_Backend:2026-06-09-41 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 2, 'Логичность и полнота представленных материалов',
       'Содержательность, информативность, глубина проработки темы, наличие основных структурных частей, логическая завершённость.', 4
FROM course_lesson cl WHERE cl.hearing_stage = 'CONFERENCE_DEFENSE'
                        AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 2);

--changeset Diplom_Backend:2026-06-09-42 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 3, 'Практическая реализуемость работы',
       'Результаты работы имеют практическое значение. Применение результатов работы позволяет достичь: экономической выгоды, улучшить экологическую ситуацию, обеспечить помощь в изучении какой-либо учебной темы и т.д.', 6
FROM course_lesson cl WHERE cl.hearing_stage = 'CONFERENCE_DEFENSE'
                        AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 3);

--changeset Diplom_Backend:2026-06-09-43 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 4, 'Внедрение в практику',
       'Степень внедрения полученных результатов в практику: наличие опытного образца, рабочей модели, программы, их апробация на целевой аудитории, применение/использование полученных экспериментальных данных, наличие путей реализации.', 5
FROM course_lesson cl WHERE cl.hearing_stage = 'CONFERENCE_DEFENSE'
                        AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 4);

--changeset Diplom_Backend:2026-06-09-44 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 5, 'Обоснование использованных методов и применение современного оборудования',
       'Обоснование выбора методов исследования, технологий изготовления, применения современного оборудования.', 3
FROM course_lesson cl WHERE cl.hearing_stage = 'CONFERENCE_DEFENSE'
                        AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 5);

--changeset Diplom_Backend:2026-06-09-45 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 6, 'Применение практических навыков в выполнении работы',
       'Изобретательность, техническая сложность, оригинальность, завершённость, качество выполнения модели, устройства, приложения, программы и т.д.', 5
FROM course_lesson cl WHERE cl.hearing_stage = 'CONFERENCE_DEFENSE'
                        AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 6);

--changeset Diplom_Backend:2026-06-09-46 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 7, 'Самостоятельность выполнения работы',
       'Соответствие уровня представляемого материала уровню понимания, показанному в ходе защиты работы (личный вклад каждого участника, владение материалом по теме).', 4
FROM course_lesson cl WHERE cl.hearing_stage = 'CONFERENCE_DEFENSE'
                        AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 7);

--changeset Diplom_Backend:2026-06-09-47 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 8, 'Умение аргументировать заключения и выводы',
       'Аргументированность собственных выводов и заключений, используя основные понятия, законы, теории, с опорой на факты общественной жизни или личный социальный опыт.', 4
FROM course_lesson cl WHERE cl.hearing_stage = 'CONFERENCE_DEFENSE'
                        AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 8);

--changeset Diplom_Backend:2026-06-09-48 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 9, 'Умение отвечать на вопросы',
       'Чёткость, обоснованность ответов на поставленные вопросы с использованием принятой терминологии, подтверждение ответа конкретными примерами, фактами.', 4
FROM course_lesson cl WHERE cl.hearing_stage = 'CONFERENCE_DEFENSE'
                        AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 9);

--changeset Diplom_Backend:2026-06-09-49 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 10, 'Культура публичного выступления',
       'Логика, грамотность (последовательность изложения материала, обозначение цели, задач, выводов), оригинальность представления, ораторское мастерство, эмоциональность, выразительность, яркость, внешний вид.', 3
FROM course_lesson cl WHERE cl.hearing_stage = 'CONFERENCE_DEFENSE'
                        AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 10);

--changeset Diplom_Backend:2026-06-09-50 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 11, 'Качество презентационных материалов',
       'Аккуратность, качество выполнения (устойчивость, надёжность конструкции / качественный звук и видеоряд), эстетика оформления, отсутствие грамматических ошибок.', 3
FROM course_lesson cl WHERE cl.hearing_stage = 'CONFERENCE_DEFENSE'
                        AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 11);

--changeset Diplom_Backend:2026-06-09-51 splitStatements:true endDelimiter:;
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 12, 'Наличие отзыва вуза/предприятия-партнёра',
       'Наличие отзыва, указывающего на полученный результат и дальнейшее развитие работы, при участии которых выполнялась работа.', 1
FROM course_lesson cl WHERE cl.hearing_stage = 'CONFERENCE_DEFENSE'
                        AND NOT EXISTS (SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 12);

-- Обновляем max_score для всех CONFERENCE_DEFENSE уроков = 45
--changeset Diplom_Backend:2026-06-09-52 splitStatements:true endDelimiter:;
UPDATE course_lesson SET max_score = 45 WHERE hearing_stage = 'CONFERENCE_DEFENSE' AND max_score != 45;