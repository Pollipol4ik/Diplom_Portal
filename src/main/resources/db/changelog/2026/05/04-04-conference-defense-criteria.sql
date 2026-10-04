--liquibase formatted sql

-- =============================================================================
-- Критерии оценивания для этапа «Защита проекта (итоговая аттестация)»
-- CONFERENCE_DEFENSE / hearing_stage = 'CONFERENCE_DEFENSE'
-- Максимум: 45 баллов (по фото с таблицей критериев МосПолитех 2024)
-- =============================================================================

--changeset Diplom_Backend:2026-05-07-01-conference-defense-criteria splitStatements:true endDelimiter:;

-- Критерий 1: Актуальность выбранной проблемы (0-3 балла)
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 1,
       'Актуальность выбранной проблемы',
       'Актуальность и значимость выбранной проблемы, наличие вариантов эффективного решения (обзор литературы и существующих подходов).',
       3
FROM course_lesson cl
WHERE cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND NOT EXISTS (
    SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 1
);

-- Критерий 2: Логичность и полнота представленных материалов (0-4 балла)
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 2,
       'Логичность и полнота представленных материалов',
       'Содержательность, информативность, глубина проработки темы, наличие основных структурных частей, логическая завершённость.',
       4
FROM course_lesson cl
WHERE cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND NOT EXISTS (
    SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 2
);

-- Критерий 3: Практическая реализуемость работы (0-6 баллов)
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 3,
       'Практическая реализуемость работы',
       'Результаты работы имеют практическое значение. Применение результатов работы позволяет достичь: экономической выгоды, улучшить экологическую ситуацию, обеспечить помощь в изучении какой-либо учебной темы и т.д.',
       6
FROM course_lesson cl
WHERE cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND NOT EXISTS (
    SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 3
);

-- Критерий 4: Внедрение в практику (0-5 баллов)
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 4,
       'Внедрение в практику',
       'Степень внедрения полученных результатов в практику: наличие опытного образца, рабочей модели, программы, их апробация на целевой аудитории, применение/использование полученных экспериментальных данных, наличие путей реализации.',
       5
FROM course_lesson cl
WHERE cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND NOT EXISTS (
    SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 4
);

-- Критерий 5: Обоснование использованных методов и применение современного оборудования (0-3 балла)
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 5,
       'Обоснование использованных методов и применение современного оборудования',
       'Обоснование выбора методов исследования, технологий изготовления, применения современного оборудования.',
       3
FROM course_lesson cl
WHERE cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND NOT EXISTS (
    SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 5
);

-- Критерий 6: Применение практических навыков в выполнении работы (0-5 баллов)
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 6,
       'Применение практических навыков в выполнении работы',
       'Изобретательность, техническая сложность, оригинальность, завершённость, качество выполнения модели, устройства, приложения, программы и т.д.',
       5
FROM course_lesson cl
WHERE cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND NOT EXISTS (
    SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 6
);

-- Критерий 7: Самостоятельность выполнения работы (0-4 балла)
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 7,
       'Самостоятельность выполнения работы',
       'Соответствие уровня представляемого материала уровню понимания, показанному в ходе защиты работы (личный вклад каждого участника, владение материалом по теме).',
       4
FROM course_lesson cl
WHERE cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND NOT EXISTS (
    SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 7
);

-- Критерий 8: Умение аргументировать заключения и выводы (0-4 балла)
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 8,
       'Умение аргументировать заключения и выводы',
       'Аргументированность собственных выводов и заключений, используя основные понятия, законы, теории, с опорой на факты общественной жизни или личный социальный опыт.',
       4
FROM course_lesson cl
WHERE cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND NOT EXISTS (
    SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 8
);

-- Критерий 9: Умение отвечать на вопросы (0-4 балла)
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 9,
       'Умение отвечать на вопросы',
       'Чёткость, обоснованность ответов на поставленные вопросы с использованием принятой терминологии, подтверждение ответа конкретными примерами, фактами.',
       4
FROM course_lesson cl
WHERE cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND NOT EXISTS (
    SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 9
);

-- Критерий 10: Культура публичного выступления (0-3 балла)
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 10,
       'Культура публичного выступления',
       'Логика, грамотность (последовательность изложения материала, обозначение цели, задач, выводов), оригинальность представления, ораторское мастерство, эмоциональность, выразительность, яркость, внешний вид.',
       3
FROM course_lesson cl
WHERE cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND NOT EXISTS (
    SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 10
);

-- Критерий 11: Качество презентационных материалов (0-3 балла)
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 11,
       'Качество презентационных материалов',
       'Аккуратность, качество выполнения (устойчивость, надёжность конструкции / качественный звук и видеоряд), эстетика оформления, отсутствие грамматических ошибок.',
       3
FROM course_lesson cl
WHERE cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND NOT EXISTS (
    SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 11
);

-- Критерий 12: Наличие отзыва вуза/предприятия-партнёра (0-1 балл)
INSERT INTO grading_criterion (lesson_id, order_number, name, description, max_points)
SELECT cl.id, 12,
       'Наличие отзыва вуза/предприятия-партнёра',
       'Наличие отзыва, указывающего на полученный результат и дальнейшее развитие работы, при участии которых выполнялась работа.',
       1
FROM course_lesson cl
WHERE cl.hearing_stage = 'CONFERENCE_DEFENSE'
  AND NOT EXISTS (
    SELECT 1 FROM grading_criterion gc WHERE gc.lesson_id = cl.id AND gc.order_number = 12
);

-- Обновляем max_score урока: сумма всех критериев = 45
UPDATE course_lesson SET max_score = 45 WHERE hearing_stage = 'CONFERENCE_DEFENSE';