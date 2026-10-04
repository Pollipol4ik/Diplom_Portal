--liquibase formatted sql

-- =============================================================================
-- Работы учеников по курсу «Введение в разработку программного обеспечения»
-- с разными статусами. 18 учеников.
-- Урок 1 (TEXT, 10б), Урок 2 (TEXT, 15б), Урок 3 (FILE→TEXT, 15б)
-- =============================================================================

-- ── Жуков Максим (@maksim.zh) — все ACCEPTED ─────────────────────────────────

--changeset Diplom_Backend:2026-06-07-01 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Agile подходит лучше: гибкость и итерации помогают быстро адаптироваться к изменениям в проекте.', 'ACCEPTED', 10, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id JOIN account a ON a.nickname = 'maksim.zh'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 1
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-07-02 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Репозиторий создан на GitHub для проекта «VPN-сервис». README оформлен, первый коммит выполнен.', 'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id JOIN account a ON a.nickname = 'maksim.zh'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 2
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-07-03 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Прототип главного экрана в Figma загружен.', 'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id JOIN account a ON a.nickname = 'maksim.zh'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 3
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

-- ── Громова Анна (@anna.grom) — урок 1 ACCEPTED, урок 2 NEEDS_REVISION, урок 3 нет ──

--changeset Diplom_Backend:2026-06-07-04 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Выбираю Agile — методология гибкая, позволяет работать итеративно и быстро получать обратную связь.', 'ACCEPTED', 10, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id JOIN account a ON a.nickname = 'anna.grom'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 1
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-07-05 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Создала репозиторий. README добавлен, но коммит пока не сделан.', 'NEEDS_REVISION', NULL, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id JOIN account a ON a.nickname = 'anna.grom'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 2
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

-- ── Сорокин Дмитрий (@dmitry.sor) — все SUBMITTED (на проверке) ──────────────

--changeset Diplom_Backend:2026-06-07-06 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Agile лучше подходит для проектной работы: позволяет работать короткими спринтами и корректировать план.', 'SUBMITTED', NULL, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id JOIN account a ON a.nickname = 'dmitry.sor'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 1
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-07-07 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Создал репозиторий на GitHub для проекта «Сканер сети». README заполнен, первый коммит выполнен.', 'SUBMITTED', NULL, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id JOIN account a ON a.nickname = 'dmitry.sor'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 2
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-07-08 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Прототип главного экрана в Figma загружен.', 'SUBMITTED', NULL, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id JOIN account a ON a.nickname = 'dmitry.sor'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 3
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

-- ── Лебедева Екатерина (@kat.leb) — урок 1 ACCEPTED, остальные нет ───────────

--changeset Diplom_Backend:2026-06-07-09 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Agile подходит лучше: итеративный подход позволяет улучшать результат на каждом шаге.', 'ACCEPTED', 10, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id JOIN account a ON a.nickname = 'kat.leb'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 1
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

-- ── Козлов Сергей (@serj.koz) — все ACCEPTED ─────────────────────────────────

--changeset Diplom_Backend:2026-06-07-10 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Для школьного проекта Agile оптимален: коротко, гибко, с регулярной обратной связью.', 'ACCEPTED', 10, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id JOIN account a ON a.nickname = 'serj.koz'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 1
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-07-11 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'GitHub репозиторий для проекта «Мониторинг доступности сервисов» создан. README написан, коммит выполнен.', 'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id JOIN account a ON a.nickname = 'serj.koz'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 2
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-07-12 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Прототип главного экрана в Figma загружен.', 'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id JOIN account a ON a.nickname = 'serj.koz'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 3
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

-- ── Новикова Ольга (@olga.nov) — урок 1 NEEDS_REVISION, урок 2 нет ───────────

--changeset Diplom_Backend:2026-06-07-13 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Waterfall лучше потому что всё сразу спланировано.', 'NEEDS_REVISION', NULL, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id JOIN account a ON a.nickname = 'olga.nov'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 1
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

-- ── Васильев Алексей (@alex.vas) — урок 1 ACCEPTED, урок 2 SUBMITTED, урок 3 нет ──

--changeset Diplom_Backend:2026-06-07-14 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Agile — правильный выбор для школьного проекта с изменяющимися требованиями.', 'ACCEPTED', 10, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id JOIN account a ON a.nickname = 'alex.vas'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 1
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-07-15 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Создал репозиторий на GitHub для проекта «Умный город». README добавлен, первый коммит сделан.', 'SUBMITTED', NULL, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id JOIN account a ON a.nickname = 'alex.vas'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 2
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

-- ── Павлова Татьяна (@tat.pavl) — все ACCEPTED ───────────────────────────────

--changeset Diplom_Backend:2026-06-07-16 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Agile позволяет работать итерациями, быстро реагировать на изменения и получать обратную связь.', 'ACCEPTED', 10, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id JOIN account a ON a.nickname = 'tat.pavl'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 1
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-07-17 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Репозиторий создан для проекта «Чат-бот для школы». README оформлен, коммит выполнен.', 'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id JOIN account a ON a.nickname = 'tat.pavl'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 2
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-07-18 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Прототип главного экрана в Figma загружен.', 'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id JOIN account a ON a.nickname = 'tat.pavl'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 3
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

-- ── Куликов Иван (@ivan.kul) — урок 1 SUBMITTED, остальные нет ───────────────

--changeset Diplom_Backend:2026-06-07-19 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Agile лучше: позволяет работать поэтапно и улучшать проект на каждом шаге.', 'SUBMITTED', NULL, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id JOIN account a ON a.nickname = 'ivan.kul'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 1
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

-- ── Белова Марина (@marina.bel) — все ACCEPTED ───────────────────────────────

--changeset Diplom_Backend:2026-06-07-20 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Agile — гибкая методология, идеальная для проектов с меняющимися требованиями.', 'ACCEPTED', 10, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id JOIN account a ON a.nickname = 'marina.bel'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 1
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-07-21 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'GitHub репозиторий для проекта «Экомониторинг». README написан, первый коммит выполнен.', 'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id JOIN account a ON a.nickname = 'marina.bel'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 2
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-07-22 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Прототип главного экрана в Figma загружен.', 'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id JOIN account a ON a.nickname = 'marina.bel'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 3
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

-- ── Тимофеев Андрей (@and.tim) — урок 1 ACCEPTED, урок 2 ACCEPTED, урок 3 NEEDS_REVISION ──

--changeset Diplom_Backend:2026-06-07-23 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Agile подходит лучше — итерации и гибкость важны при работе над школьными проектами.', 'ACCEPTED', 10, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id JOIN account a ON a.nickname = 'and.tim'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 1
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-07-24 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Репозиторий создан, README оформлен. Ссылка: github.com/and.tim/network-project', 'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id JOIN account a ON a.nickname = 'and.tim'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 2
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-07-25 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Прототип в Figma сделан, но экран пустой без контента.', 'NEEDS_REVISION', NULL, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id JOIN account a ON a.nickname = 'and.tim'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 3
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

-- ── Крюкова Елена (@elena.kryu) — только урок 1 SUBMITTED ────────────────────

--changeset Diplom_Backend:2026-06-07-26 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Agile позволяет гибко управлять задачами, что важно при работе в команде.', 'SUBMITTED', NULL, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id JOIN account a ON a.nickname = 'elena.kryu'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 1
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

-- ── Дьяков Владимир (@vlad.dya) — все ACCEPTED ───────────────────────────────

--changeset Diplom_Backend:2026-06-07-27 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Agile подходит лучше — итерации, гибкость, быстрая обратная связь.', 'ACCEPTED', 10, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id JOIN account a ON a.nickname = 'vlad.dya'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 1
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-07-28 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Репозиторий на GitHub создан для проекта «IoT-платформа». README написан, коммит выполнен.', 'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id JOIN account a ON a.nickname = 'vlad.dya'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 2
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-07-29 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Прототип главного экрана в Figma загружен.', 'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id JOIN account a ON a.nickname = 'vlad.dya'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 3
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

-- ── Миронова Наталья (@nat.mir) — урок 1 ACCEPTED, урок 2 SUBMITTED ──────────

--changeset Diplom_Backend:2026-06-07-30 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Выбираю Agile: короткие спринты помогают сохранять мотивацию и двигаться вперёд.', 'ACCEPTED', 10, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id JOIN account a ON a.nickname = 'nat.mir'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 1
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-07-31 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Репозиторий создан на GitHub. README написан, первый коммит сделан.', 'SUBMITTED', NULL, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id JOIN account a ON a.nickname = 'nat.mir'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 2
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

-- ── Сергеев Павел (@pavel.ser) — все ACCEPTED ────────────────────────────────

--changeset Diplom_Backend:2026-06-07-32 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Agile — единственный выбор для динамичной командной работы. Waterfall слишком жёсткий.', 'ACCEPTED', 10, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id JOIN account a ON a.nickname = 'pavel.ser'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 1
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-07-33 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'GitHub репозиторий создан для проекта «Веб-панель мониторинга». README оформлен, коммит выполнен.', 'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id JOIN account a ON a.nickname = 'pavel.ser'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 2
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-07-34 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Прототип главного экрана в Figma загружен.', 'ACCEPTED', 15, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id JOIN account a ON a.nickname = 'pavel.ser'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 3
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

-- ── Еремина Ирина (@irina.ere) — урок 1 NEEDS_REVISION, урок 2 нет ───────────

--changeset Diplom_Backend:2026-06-07-35 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Waterfall лучше, потому что план сразу понятен.', 'NEEDS_REVISION', NULL, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id JOIN account a ON a.nickname = 'irina.ere'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 1
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

-- ── Борисов Никита (@nik.bor) — все SUBMITTED ────────────────────────────────

--changeset Diplom_Backend:2026-06-07-36 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Agile: итерации, гибкость, командная работа — всё это нужно для успешного проекта.', 'SUBMITTED', NULL, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id JOIN account a ON a.nickname = 'nik.bor'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 1
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

--changeset Diplom_Backend:2026-06-07-37 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Создал репозиторий. README добавлен, коммит сделан.', 'SUBMITTED', NULL, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id JOIN account a ON a.nickname = 'nik.bor'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 2
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);

-- ── Денисова Юлия (@yulia.den) — урок 1 ACCEPTED, остальные нет ──────────────

--changeset Diplom_Backend:2026-06-07-38 splitStatements:true endDelimiter:;
INSERT INTO lesson_submission (lesson_id, account_id, text_content, status, score, submitted_at, updated_at)
SELECT cl.id, a.id, 'Agile подходит лучше: позволяет регулярно улучшать продукт и быстро реагировать на изменения.', 'ACCEPTED', 10, NOW(), NOW()
FROM course_lesson cl JOIN course c ON cl.course_id = c.id JOIN account a ON a.nickname = 'yulia.den'
WHERE c.name = 'Введение в разработку программного обеспечения' AND cl.order_number = 1
  AND NOT EXISTS (SELECT 1 FROM lesson_submission ls WHERE ls.lesson_id = cl.id AND ls.account_id = a.id);
