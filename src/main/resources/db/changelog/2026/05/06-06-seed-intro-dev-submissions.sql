--liquibase formatted sql

-- =============================================================================
-- Выполненные работы учеников по курсу «Введение в разработку программного обеспечения».
-- Все три урока: Жизненный цикл (order_number=1), GIT (order_number=2), Figma (order_number=3).
-- Статус ACCEPTED, баллы по максимуму: 10 + 15 + 15 = 40 на ученика.
-- Все INSERT идемпотентны через WHERE NOT EXISTS.
-- =============================================================================

-- =============================================================================
-- Вспомогательная процедура: вставка одной сдачи по нику и порядковому номеру урока
-- (inline через subquery — без хранимой процедуры для совместимости с Liquibase)
-- =============================================================================

-- ── Смирнов Иван (@ivan.smirnov) ─────────────────────────────────────────────

--changeset Diplom_Backend:2026-06-06-01-sub-ivan-l1 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id,
       'Для проектной деятельности лучше подходит Agile: задачи меняются по ходу работы, и возможность итеративно улучшать продукт важна. Waterfall подходит для стабильных требований, но в школьных проектах это редкость.',
       'ACCEPTED', 10, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'ivan.smirnov'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 1
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-06-02-sub-ivan-l2 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id,
       'Создал репозиторий на GitHub: https://github.com/ivan.smirnov/it-project. Добавил README.md с описанием проекта «Система учёта задач». Первый коммит выполнен.',
       'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'ivan.smirnov'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 2
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-06-03-sub-ivan-l3 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Прототип главного экрана в Figma загружен.', 'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'ivan.smirnov'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 3
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

-- ── Козлова Ольга (@olga.kozlova) ────────────────────────────────────────────

--changeset Diplom_Backend:2026-06-06-04-sub-olga-l1 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id,
       'Agile подходит лучше, потому что позволяет адаптироваться к изменениям в процессе разработки. Waterfall требует чёткого плана с самого начала, что сложно при неопределённых требованиях.',
       'ACCEPTED', 10, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'olga.kozlova'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 1
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-06-05-sub-olga-l2 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id,
       'Репозиторий создан на GitHub. README содержит описание проекта «Мобильное расписание». Ссылка: https://github.com/olga.kozlova/schedule-app',
       'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'olga.kozlova'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 2
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-06-06-sub-olga-l3 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Прототип главного экрана в Figma загружен.', 'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'olga.kozlova'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 3
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

-- ── Николаев Дмитрий (@dmitry.n) ─────────────────────────────────────────────

--changeset Diplom_Backend:2026-06-06-07-sub-dmitry-l1 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id,
       'Agile — гибкая методология, позволяющая вносить изменения на любом этапе. Для школьного проекта это важно, так как идея может меняться. Waterfall подходит, когда требования зафиксированы заранее.',
       'ACCEPTED', 10, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'dmitry.n'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 1
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-06-08-sub-dmitry-l2 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id,
       'Создал репозиторий на GitLab для проекта «Умный дом». Добавил README и сделал первый коммит с базовой структурой папок.',
       'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'dmitry.n'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 2
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-06-09-sub-dmitry-l3 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Прототип главного экрана в Figma загружен.', 'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'dmitry.n'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 3
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

-- ── Соколова Екатерина (@kat.sokolova) ───────────────────────────────────────

--changeset Diplom_Backend:2026-06-06-10-sub-kat-l1 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id,
       'Выбираю Agile: короткие итерации помогают быстро получать обратную связь и улучшать проект. Waterfall сложнее применять, когда требования ещё не до конца понятны.',
       'ACCEPTED', 10, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'kat.sokolova'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 1
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-06-11-sub-kat-l2 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id,
       'GitHub репозиторий создан для проекта «Приложение для трекинга привычек». README с описанием и первый коммит выполнены.',
       'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'kat.sokolova'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 2
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-06-12-sub-kat-l3 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Прототип главного экрана в Figma загружен.', 'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'kat.sokolova'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 3
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

-- ── Петров Алексей (@alex.petrov) ────────────────────────────────────────────

--changeset Diplom_Backend:2026-06-06-13-sub-apetrov-l1 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id,
       'Agile позволяет работать итеративно и реагировать на изменения. Waterfall хорош для проектов с чёткими требованиями. Для нашего проекта подойдёт Agile.',
       'ACCEPTED', 10, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'alex.petrov'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 1
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-06-14-sub-apetrov-l2 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id,
       'Создан репозиторий на GitHub для проекта «Веб-сайт школьного клуба». README оформлен, первый коммит сделан.',
       'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'alex.petrov'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 2
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-06-15-sub-apetrov-l3 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Прототип главного экрана в Figma загружен.', 'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'alex.petrov'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 3
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

-- ── Михайлова Анна (@ann.mikh) ───────────────────────────────────────────────

--changeset Diplom_Backend:2026-06-06-16-sub-ann-l1 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id,
       'Agile лучше подходит для нашего проекта: можно менять функциональность по ходу разработки. Waterfall подходит для крупных корпоративных проектов с фиксированными требованиями.',
       'ACCEPTED', 10, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'ann.mikh'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 1
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-06-17-sub-ann-l2 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id,
       'Репозиторий на GitHub создан для проекта «Чат-бот для школы». README заполнен, первый коммит выполнен.',
       'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'ann.mikh'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 2
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-06-18-sub-ann-l3 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Прототип главного экрана в Figma загружен.', 'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'ann.mikh'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 3
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

-- ── Фёдоров Сергей (@s.fedorov) ──────────────────────────────────────────────

--changeset Diplom_Backend:2026-06-06-19-sub-sfed-l1 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id,
       'Для нашего проекта выберу Agile — позволяет работать спринтами и быстро адаптироваться. Waterfall требует полного планирования заранее, что не всегда реально.',
       'ACCEPTED', 10, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 's.fedorov'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 1
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-06-20-sub-sfed-l2 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id,
       'Репозиторий создан на GitHub для проекта «Платформа для обмена учебными материалами». README с описанием. Первый коммит выполнен.',
       'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 's.fedorov'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 2
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-06-21-sub-sfed-l3 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Прототип главного экрана в Figma загружен.', 'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 's.fedorov'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 3
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

-- ── Васильева Елена (@el.vasil) ──────────────────────────────────────────────

--changeset Diplom_Backend:2026-06-06-22-sub-elvas-l1 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id,
       'Agile — лучший выбор для гибкой работы над проектом. Позволяет получать промежуточные результаты и улучшать их. Waterfall не гибок и плохо работает при изменяющихся требованиях.',
       'ACCEPTED', 10, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'el.vasil'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 1
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-06-23-sub-elvas-l2 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id,
       'Создала репозиторий на GitHub для проекта «Карта достопримечательностей района». README оформлен, первый коммит сделан.',
       'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'el.vasil'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 2
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-06-24-sub-elvas-l3 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Прототип главного экрана в Figma загружен.', 'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'el.vasil'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 3
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

-- ── Павлов Андрей (@and.pavlov) ──────────────────────────────────────────────

--changeset Diplom_Backend:2026-06-06-25-sub-andpav-l1 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id,
       'Agile подходит лучше — разработка идёт итеративно, команда может быстро реагировать на новые требования. Waterfall подходит для проектов с полностью определёнными требованиями на старте.',
       'ACCEPTED', 10, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'and.pavlov'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 1
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-06-26-sub-andpav-l2 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id,
       'Создан репозиторий на GitHub для проекта «Генератор тестов». README оформлен, первый коммит выполнен.',
       'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'and.pavlov'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 2
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-06-27-sub-andpav-l3 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Прототип главного экрана в Figma загружен.', 'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'and.pavlov'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 3
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

-- ── Егорова Мария (@mar.egor) ─────────────────────────────────────────────────

--changeset Diplom_Backend:2026-06-06-28-sub-maregor-l1 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id,
       'Для нашего проекта выбираю Agile: можно легко изменять приоритеты и добавлять функции по мере необходимости. Waterfall не позволяет гибко реагировать на изменения.',
       'ACCEPTED', 10, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'mar.egor'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 1
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-06-29-sub-maregor-l2 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id,
       'GitHub репозиторий создан для проекта «Сервис визуализации алгоритмов». README написан, первый коммит сделан.',
       'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'mar.egor'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 2
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-06-30-sub-maregor-l3 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Прототип главного экрана в Figma загружен.', 'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'mar.egor'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 3
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

-- ── Семёнов Николай (@nik.semen) ─────────────────────────────────────────────

--changeset Diplom_Backend:2026-06-06-31-sub-niksem-l1 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id,
       'Agile подходит, так как позволяет работать итерациями и постепенно улучшать продукт. Waterfall лучше для крупных проектов с предсказуемыми требованиями.',
       'ACCEPTED', 10, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'nik.semen'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 1
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-06-32-sub-niksem-l2 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id,
       'Репозиторий на GitLab создан для проекта «Умная система полива». README заполнен, первый коммит выполнен.',
       'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'nik.semen'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 2
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-06-33-sub-niksem-l3 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Прототип главного экрана в Figma загружен.', 'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'nik.semen'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 3
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

-- ── Иванова Татьяна (@tat.ivan) ──────────────────────────────────────────────

--changeset Diplom_Backend:2026-06-06-34-sub-tativan-l1 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id,
       'Выбираю Agile: методология ориентирована на сотрудничество и быструю адаптацию. Waterfall сложен при изменении требований в процессе разработки.',
       'ACCEPTED', 10, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'tat.ivan'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 1
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-06-35-sub-tativan-l2 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id,
       'Создала репозиторий на GitHub для проекта «Цифровой архив школы». README оформлен, первый коммит выполнен.',
       'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'tat.ivan'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 2
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-06-36-sub-tativan-l3 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Прототип главного экрана в Figma загружен.', 'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'tat.ivan'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 3
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

-- ── Морозов Владимир (@vlad.mor) ─────────────────────────────────────────────

--changeset Diplom_Backend:2026-06-06-37-sub-vladmor-l1 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id,
       'Agile позволяет работать небольшими итерациями и быстро реагировать на изменения. Для проекта, где требования уточняются по ходу работы, это оптимальный выбор.',
       'ACCEPTED', 10, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'vlad.mor'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 1
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-06-38-sub-vladmor-l2 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id,
       'GitHub репозиторий создан для проекта «Мониторинг сети». README написан, выполнен первый коммит.',
       'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'vlad.mor'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 2
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-06-39-sub-vladmor-l3 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Прототип главного экрана в Figma загружен.', 'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'vlad.mor'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 3
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

-- ── Новикова Надежда (@nad.nov) ───────────────────────────────────────────────

--changeset Diplom_Backend:2026-06-06-40-sub-nadnov-l1 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id,
       'Agile лучше подходит для школьного проекта: гибкость и регулярные демонстрации результатов помогают двигаться вперёд. Waterfall не оставляет места для изменений.',
       'ACCEPTED', 10, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'nad.nov'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 1
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-06-41-sub-nadnov-l2 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id,
       'Репозиторий на GitHub создан для проекта «Платформа мероприятий». README оформлен, первый коммит выполнен.',
       'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'nad.nov'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 2
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-06-42-sub-nadnov-l3 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Прототип главного экрана в Figma загружен.', 'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'nad.nov'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 3
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

-- ── Волков Юрий (@yur.volk) ──────────────────────────────────────────────────

--changeset Diplom_Backend:2026-06-06-43-sub-yurvolk-l1 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id,
       'Agile — оптимальный выбор, поскольку позволяет работать короткими итерациями и получать обратную связь после каждой. Waterfall подходит для проектов с предсказуемым результатом.',
       'ACCEPTED', 10, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'yur.volk'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 1
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-06-44-sub-yurvolk-l2 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id,
       'GitHub репозиторий создан для проекта «Чат-мессенджер в локальной сети». README заполнен, первый коммит сделан.',
       'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'yur.volk'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 2
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-06-45-sub-yurvolk-l3 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Прототип главного экрана в Figma загружен.', 'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'yur.volk'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 3
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

-- ── Зайцева Ирина (@ir.zayt) ──────────────────────────────────────────────────

--changeset Diplom_Backend:2026-06-06-46-sub-irzayt-l1 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id,
       'Agile подходит лучше: короткие циклы разработки позволяют быстро исправлять ошибки. Waterfall не даёт вернуться к предыдущему этапу без лишних затрат.',
       'ACCEPTED', 10, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'ir.zayt'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 1
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-06-47-sub-irzayt-l2 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id,
       'Создала репозиторий на GitHub для проекта «AR-приложение». README написан, первый коммит выполнен.',
       'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'ir.zayt'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 2
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-06-48-sub-irzayt-l3 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Прототип главного экрана в Figma загружен.', 'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'ir.zayt'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 3
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

-- ── Кузнецов Михаил (@mikhail.kuz) ───────────────────────────────────────────

--changeset Diplom_Backend:2026-06-06-49-sub-mikhkuz-l1 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id,
       'Agile лучше подходит для итеративной разработки. Waterfall хорош только при стабильных и полностью определённых требованиях, что редко встречается в реальных проектах.',
       'ACCEPTED', 10, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'mikhail.kuz'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 1
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-06-50-sub-mikhkuz-l2 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id,
       'Репозиторий GitHub создан для проекта «2D-платформер». README оформлен, первый коммит выполнен.',
       'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'mikhail.kuz'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 2
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-06-51-sub-mikhkuz-l3 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Прототип главного экрана в Figma загружен.', 'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'mikhail.kuz'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 3
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

-- ── Попова Виктория (@vika.pop) ──────────────────────────────────────────────

--changeset Diplom_Backend:2026-06-06-52-sub-vikapop-l1 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id,
       'Agile — гибкий подход с быстрыми итерациями. Для нашего проекта предпочтительнее, так как идея может развиваться. Waterfall слишком жёсткий для творческих задач.',
       'ACCEPTED', 10, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'vika.pop'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 1
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-06-53-sub-vikapop-l2 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id,
       'Создала репозиторий на GitHub для проекта «Трекер привычек». README написан, первый коммит сделан.',
       'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'vika.pop'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 2
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-06-54-sub-vikapop-l3 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Прототип главного экрана в Figma загружен.', 'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'vika.pop'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 3
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

-- ── Романов Александр (@alex.rom) ────────────────────────────────────────────

--changeset Diplom_Backend:2026-06-06-55-sub-alexrom-l1 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id,
       'Agile — лучший выбор: позволяет быстро выпускать рабочие версии и получать обратную связь. Waterfall требует детального планирования, которое сложно выполнить на начальном этапе.',
       'ACCEPTED', 10, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'alex.rom'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 1
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-06-56-sub-alexrom-l2 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id,
       'Репозиторий на GitHub создан для проекта «Робот-сортировщик». README оформлен, первый коммит сделан.',
       'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'alex.rom'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 2
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-06-57-sub-alexrom-l3 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Прототип главного экрана в Figma загружен.', 'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'alex.rom'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 3
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

-- ── Григорьева Людмила (@luda.grig) ──────────────────────────────────────────

--changeset Diplom_Backend:2026-06-06-58-sub-ludagrig-l1 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id,
       'Выбираю Agile — методология позволяет команде работать слаженно и адаптироваться к изменениям. Waterfall слишком линеен для проектов, где требования уточняются.',
       'ACCEPTED', 10, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'luda.grig'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 1
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-06-59-sub-ludagrig-l2 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id,
       'GitHub репозиторий создан для проекта «Интерактивная карта». README написан, первый коммит выполнен.',
       'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'luda.grig'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 2
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-06-60-sub-ludagrig-l3 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Прототип главного экрана в Figma загружен.', 'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'luda.grig'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 3
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

-- ── Белов Антон (@anton.bel) ─────────────────────────────────────────────────

--changeset Diplom_Backend:2026-06-06-61-sub-antonbel-l1 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id,
       'Agile подходит лучше: итеративный подход позволяет постоянно улучшать продукт и оперативно реагировать на замечания. Waterfall требует полного технического задания с самого начала.',
       'ACCEPTED', 10, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'anton.bel'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 1
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-06-62-sub-antonbel-l2 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id,
       'Создал репозиторий на GitHub для проекта «CRM-система». README оформлен, первый коммит выполнен.',
       'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'anton.bel'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 2
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-06-63-sub-antonbel-l3 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Прототип главного экрана в Figma загружен.', 'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'anton.bel'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 3
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

-- ── Орлова Елизавета (@liza.orl) ─────────────────────────────────────────────

--changeset Diplom_Backend:2026-06-06-64-sub-lizaorl-l1 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id,
       'Agile лучше подходит: позволяет вносить изменения в любой момент и показывать промежуточные результаты. Waterfall не гибок при появлении новых идей в процессе разработки.',
       'ACCEPTED', 10, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'liza.orl'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 1
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-06-65-sub-lizaorl-l2 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id,
       'GitHub репозиторий создан для проекта «VR-тренажёр». README написан, первый коммит сделан.',
       'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'liza.orl'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 2
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-06-66-sub-lizaorl-l3 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Прототип главного экрана в Figma загружен.', 'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'liza.orl'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 3
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

-- ── Макарова Дарья (@dar.mak) ─────────────────────────────────────────────────

--changeset Diplom_Backend:2026-06-06-67-sub-darmak-l1 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id,
       'Выбираю Agile: гибкость и регулярная обратная связь важны для успешного завершения проекта. Waterfall подходит для хорошо определённых задач с фиксированными требованиями.',
       'ACCEPTED', 10, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'dar.mak'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 1
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-06-68-sub-darmak-l2 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id,
       'Репозиторий на GitHub создан для проекта «Учёт складских остатков». README оформлен, первый коммит выполнен.',
       'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'dar.mak'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 2
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-06-69-sub-darmak-l3 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Прототип главного экрана в Figma загружен.', 'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'dar.mak'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 3
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

-- ── Медведев Артём (@art.med) ─────────────────────────────────────────────────

--changeset Diplom_Backend:2026-06-06-70-sub-artmed-l1 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id,
       'Agile — лучший выбор для проектной работы. Итеративный подход позволяет быстро получать рабочий результат и улучшать его. Waterfall не подходит для творческих проектов.',
       'ACCEPTED', 10, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'art.med'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 1
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-06-71-sub-artmed-l2 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id,
       'GitHub репозиторий создан для проекта «Умный город». README написан, первый коммит выполнен.',
       'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'art.med'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 2
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-06-72-sub-artmed-l3 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Прототип главного экрана в Figma загружен.', 'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'art.med'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 3
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

-- ── Андреева Ксения (@ksu.and) ────────────────────────────────────────────────

--changeset Diplom_Backend:2026-06-06-73-sub-ksuand-l1 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id,
       'Agile подходит лучше всего: гибкость, командная работа и постоянное улучшение продукта. Waterfall оправдан только в проектах с жёстко зафиксированными требованиями.',
       'ACCEPTED', 10, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'ksu.and'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 1
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-06-74-sub-ksuand-l2 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id,
       'Репозиторий на GitHub создан для проекта «Мобильное приложение для изучения слов». README оформлен, первый коммит выполнен.',
       'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'ksu.and'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 2
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-06-75-sub-ksuand-l3 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Прототип главного экрана в Figma загружен.', 'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id
                      JOIN account a ON a.nickname = 'ksu.and'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 3
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);