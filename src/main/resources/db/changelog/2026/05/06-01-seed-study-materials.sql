
-- ── 1. Направление ───────────────────────────────────────────────────────────

--changeset Diplom_Backend:2026-06-01-01-direction-study-materials splitStatements:true endDelimiter:;
INSERT INTO direction (name)
SELECT 'Учебные материалы'
WHERE NOT EXISTS (SELECT 1 FROM direction WHERE name = 'Учебные материалы');

-- ── 2. Предметы ──────────────────────────────────────────────────────────────

--changeset Diplom_Backend:2026-06-01-02-subject-web splitStatements:true endDelimiter:;
INSERT INTO subject (name, direction_id)
SELECT 'Веб-разработка', d.id
FROM direction d
WHERE d.name = 'Учебные материалы'
  AND NOT EXISTS (
    SELECT 1 FROM subject s WHERE s.name = 'Веб-разработка' AND s.direction_id = d.id
);

--changeset Diplom_Backend:2026-06-01-03-subject-db splitStatements:true endDelimiter:;
INSERT INTO subject (name, direction_id)
SELECT 'Базы данных', d.id
FROM direction d
WHERE d.name = 'Учебные материалы'
  AND NOT EXISTS (
    SELECT 1 FROM subject s WHERE s.name = 'Базы данных' AND s.direction_id = d.id
);

--changeset Diplom_Backend:2026-06-01-04-subject-tools splitStatements:true endDelimiter:;
INSERT INTO subject (name, direction_id)
SELECT 'Инструменты разработки', d.id
FROM direction d
WHERE d.name = 'Учебные материалы'
  AND NOT EXISTS (
    SELECT 1 FROM subject s WHERE s.name = 'Инструменты разработки' AND s.direction_id = d.id
);

--changeset Diplom_Backend:2026-06-01-05-subject-desktop splitStatements:true endDelimiter:;
INSERT INTO subject (name, direction_id)
SELECT 'Desktop-разработка', d.id
FROM direction d
WHERE d.name = 'Учебные материалы'
  AND NOT EXISTS (
    SELECT 1 FROM subject s WHERE s.name = 'Desktop-разработка' AND s.direction_id = d.id
);

--changeset Diplom_Backend:2026-06-01-06-subject-methodics splitStatements:true endDelimiter:;
INSERT INTO subject (name, direction_id)
SELECT 'Методические материалы', d.id
FROM direction d
WHERE d.name = 'Учебные материалы'
  AND NOT EXISTS (
    SELECT 1 FROM subject s WHERE s.name = 'Методические материалы' AND s.direction_id = d.id
);

-- ── 3. Темы ──────────────────────────────────────────────────────────────────

-- Веб-разработка
--changeset Diplom_Backend:2026-06-01-07-topic-html splitStatements:true endDelimiter:;
INSERT INTO subject_topic (name, subject_id)
SELECT 'HTML, CSS, JavaScript', s.id
FROM subject s JOIN direction d ON s.direction_id = d.id
WHERE d.name = 'Учебные материалы' AND s.name = 'Веб-разработка'
  AND NOT EXISTS (
    SELECT 1 FROM subject_topic t WHERE t.name = 'HTML, CSS, JavaScript' AND t.subject_id = s.id
);

--changeset Diplom_Backend:2026-06-01-08-topic-bootstrap splitStatements:true endDelimiter:;
INSERT INTO subject_topic (name, subject_id)
SELECT 'Bootstrap', s.id
FROM subject s JOIN direction d ON s.direction_id = d.id
WHERE d.name = 'Учебные материалы' AND s.name = 'Веб-разработка'
  AND NOT EXISTS (
    SELECT 1 FROM subject_topic t WHERE t.name = 'Bootstrap' AND t.subject_id = s.id
);

--changeset Diplom_Backend:2026-06-01-09-topic-php splitStatements:true endDelimiter:;
INSERT INTO subject_topic (name, subject_id)
SELECT 'PHP', s.id
FROM subject s JOIN direction d ON s.direction_id = d.id
WHERE d.name = 'Учебные материалы' AND s.name = 'Веб-разработка'
  AND NOT EXISTS (
    SELECT 1 FROM subject_topic t WHERE t.name = 'PHP' AND t.subject_id = s.id
);

--changeset Diplom_Backend:2026-06-01-10-topic-django splitStatements:true endDelimiter:;
INSERT INTO subject_topic (name, subject_id)
SELECT 'Django', s.id
FROM subject s JOIN direction d ON s.direction_id = d.id
WHERE d.name = 'Учебные материалы' AND s.name = 'Веб-разработка'
  AND NOT EXISTS (
    SELECT 1 FROM subject_topic t WHERE t.name = 'Django' AND t.subject_id = s.id
);

-- Базы данных
--changeset Diplom_Backend:2026-06-01-11-topic-mysql splitStatements:true endDelimiter:;
INSERT INTO subject_topic (name, subject_id)
SELECT 'MySQL', s.id
FROM subject s JOIN direction d ON s.direction_id = d.id
WHERE d.name = 'Учебные материалы' AND s.name = 'Базы данных'
  AND NOT EXISTS (
    SELECT 1 FROM subject_topic t WHERE t.name = 'MySQL' AND t.subject_id = s.id
);

--changeset Diplom_Backend:2026-06-01-12-topic-postgresql splitStatements:true endDelimiter:;
INSERT INTO subject_topic (name, subject_id)
SELECT 'PostgreSQL', s.id
FROM subject s JOIN direction d ON s.direction_id = d.id
WHERE d.name = 'Учебные материалы' AND s.name = 'Базы данных'
  AND NOT EXISTS (
    SELECT 1 FROM subject_topic t WHERE t.name = 'PostgreSQL' AND t.subject_id = s.id
);

-- Инструменты разработки
--changeset Diplom_Backend:2026-06-01-13-topic-git splitStatements:true endDelimiter:;
INSERT INTO subject_topic (name, subject_id)
SELECT 'GIT', s.id
FROM subject s JOIN direction d ON s.direction_id = d.id
WHERE d.name = 'Учебные материалы' AND s.name = 'Инструменты разработки'
  AND NOT EXISTS (
    SELECT 1 FROM subject_topic t WHERE t.name = 'GIT' AND t.subject_id = s.id
);

--changeset Diplom_Backend:2026-06-01-14-topic-figma splitStatements:true endDelimiter:;
INSERT INTO subject_topic (name, subject_id)
SELECT 'Figma', s.id
FROM subject s JOIN direction d ON s.direction_id = d.id
WHERE d.name = 'Учебные материалы' AND s.name = 'Инструменты разработки'
  AND NOT EXISTS (
    SELECT 1 FROM subject_topic t WHERE t.name = 'Figma' AND t.subject_id = s.id
);

-- Desktop-разработка
--changeset Diplom_Backend:2026-06-01-15-topic-wpf splitStatements:true endDelimiter:;
INSERT INTO subject_topic (name, subject_id)
SELECT 'WPF C#', s.id
FROM subject s JOIN direction d ON s.direction_id = d.id
WHERE d.name = 'Учебные материалы' AND s.name = 'Desktop-разработка'
  AND NOT EXISTS (
    SELECT 1 FROM subject_topic t WHERE t.name = 'WPF C#' AND t.subject_id = s.id
);

-- Методические материалы
--changeset Diplom_Backend:2026-06-01-16-topic-checklists splitStatements:true endDelimiter:;
INSERT INTO subject_topic (name, subject_id)
SELECT 'Чек-листы и шаблоны', s.id
FROM subject s JOIN direction d ON s.direction_id = d.id
WHERE d.name = 'Учебные материалы' AND s.name = 'Методические материалы'
  AND NOT EXISTS (
    SELECT 1 FROM subject_topic t WHERE t.name = 'Чек-листы и шаблоны' AND t.subject_id = s.id
);

-- ── 4. Публикации и привязка к темам ─────────────────────────────────────────

-- PHP
--changeset Diplom_Backend:2026-06-01-17-pub-php-manual splitStatements:true endDelimiter:;
INSERT INTO publication (title, description, account_id, supports_thread, created_at)
SELECT 'PHP — Методическое пособие',
       'Методическое пособие по языку программирования PHP. Ссылка: https://disk.yandex.by/i/kQNFQSeBKd9Buw',
       1, FALSE, now()
WHERE NOT EXISTS (SELECT 1 FROM publication WHERE title = 'PHP — Методическое пособие');

--changeset Diplom_Backend:2026-06-01-18-pub-php-manual-link splitStatements:true endDelimiter:;
INSERT INTO subject_topic_publication (publication_id, subject_topic_id)
SELECT p.id, t.id
FROM publication p, subject_topic t
                        JOIN subject s ON t.subject_id = s.id
                        JOIN direction d ON s.direction_id = d.id
WHERE p.title = 'PHP — Методическое пособие'
  AND t.name = 'PHP'
  AND s.name = 'Веб-разработка'
  AND d.name = 'Учебные материалы'
  AND NOT EXISTS (
    SELECT 1 FROM subject_topic_publication stp
    WHERE stp.publication_id = p.id AND stp.subject_topic_id = t.id
);

-- GIT
--changeset Diplom_Backend:2026-06-01-19-pub-git-manual splitStatements:true endDelimiter:;
INSERT INTO publication (title, description, account_id, supports_thread, created_at)
SELECT 'GIT — Методическое пособие',
       'Методическое пособие по системе контроля версий GIT. Ссылка: https://disk.yandex.ru/i/DhN6jO0m6ygN9A',
       1, FALSE, now()
WHERE NOT EXISTS (SELECT 1 FROM publication WHERE title = 'GIT — Методическое пособие');

--changeset Diplom_Backend:2026-06-01-20-pub-git-manual-link splitStatements:true endDelimiter:;
INSERT INTO subject_topic_publication (publication_id, subject_topic_id)
SELECT p.id, t.id
FROM publication p, subject_topic t
                        JOIN subject s ON t.subject_id = s.id
                        JOIN direction d ON s.direction_id = d.id
WHERE p.title = 'GIT — Методическое пособие'
  AND t.name = 'GIT'
  AND s.name = 'Инструменты разработки'
  AND d.name = 'Учебные материалы'
  AND NOT EXISTS (
    SELECT 1 FROM subject_topic_publication stp
    WHERE stp.publication_id = p.id AND stp.subject_topic_id = t.id
);

--changeset Diplom_Backend:2026-06-01-21-pub-git-slides splitStatements:true endDelimiter:;
INSERT INTO publication (title, description, account_id, supports_thread, created_at)
SELECT 'GIT — Презентация',
       'Презентация по системе контроля версий GIT. Ссылка: https://disk.yandex.by/i/x5_vWQOTLnlxFw',
       1, FALSE, now()
WHERE NOT EXISTS (SELECT 1 FROM publication WHERE title = 'GIT — Презентация');

--changeset Diplom_Backend:2026-06-01-22-pub-git-slides-link splitStatements:true endDelimiter:;
INSERT INTO subject_topic_publication (publication_id, subject_topic_id)
SELECT p.id, t.id
FROM publication p, subject_topic t
                        JOIN subject s ON t.subject_id = s.id
                        JOIN direction d ON s.direction_id = d.id
WHERE p.title = 'GIT — Презентация'
  AND t.name = 'GIT'
  AND s.name = 'Инструменты разработки'
  AND d.name = 'Учебные материалы'
  AND NOT EXISTS (
    SELECT 1 FROM subject_topic_publication stp
    WHERE stp.publication_id = p.id AND stp.subject_topic_id = t.id
);

-- MySQL
--changeset Diplom_Backend:2026-06-01-23-pub-mysql-manual splitStatements:true endDelimiter:;
INSERT INTO publication (title, description, account_id, supports_thread, created_at)
SELECT 'MySQL — Методическое пособие',
       'Методическое пособие по СУБД MySQL. Ссылка: https://disk.yandex.ru/i/fpO8JdnwAe3CGQ',
       1, FALSE, now()
WHERE NOT EXISTS (SELECT 1 FROM publication WHERE title = 'MySQL — Методическое пособие');

--changeset Diplom_Backend:2026-06-01-24-pub-mysql-manual-link splitStatements:true endDelimiter:;
INSERT INTO subject_topic_publication (publication_id, subject_topic_id)
SELECT p.id, t.id
FROM publication p, subject_topic t
                        JOIN subject s ON t.subject_id = s.id
                        JOIN direction d ON s.direction_id = d.id
WHERE p.title = 'MySQL — Методическое пособие'
  AND t.name = 'MySQL'
  AND s.name = 'Базы данных'
  AND d.name = 'Учебные материалы'
  AND NOT EXISTS (
    SELECT 1 FROM subject_topic_publication stp
    WHERE stp.publication_id = p.id AND stp.subject_topic_id = t.id
);

--changeset Diplom_Backend:2026-06-01-25-pub-mysql-slides splitStatements:true endDelimiter:;
INSERT INTO publication (title, description, account_id, supports_thread, created_at)
SELECT 'MySQL — Презентация',
       'Презентация по СУБД MySQL. Ссылка: https://disk.yandex.ru/i/z86fuZhpaRoxSw',
       1, FALSE, now()
WHERE NOT EXISTS (SELECT 1 FROM publication WHERE title = 'MySQL — Презентация');

--changeset Diplom_Backend:2026-06-01-26-pub-mysql-slides-link splitStatements:true endDelimiter:;
INSERT INTO subject_topic_publication (publication_id, subject_topic_id)
SELECT p.id, t.id
FROM publication p, subject_topic t
                        JOIN subject s ON t.subject_id = s.id
                        JOIN direction d ON s.direction_id = d.id
WHERE p.title = 'MySQL — Презентация'
  AND t.name = 'MySQL'
  AND s.name = 'Базы данных'
  AND d.name = 'Учебные материалы'
  AND NOT EXISTS (
    SELECT 1 FROM subject_topic_publication stp
    WHERE stp.publication_id = p.id AND stp.subject_topic_id = t.id
);

-- PostgreSQL
--changeset Diplom_Backend:2026-06-01-27-pub-pg-manual splitStatements:true endDelimiter:;
INSERT INTO publication (title, description, account_id, supports_thread, created_at)
SELECT 'PostgreSQL — Методическое пособие',
       'Методическое пособие по СУБД PostgreSQL. Ссылка: https://disk.yandex.ru/i/NV2e1kHSuRmDQg',
       1, FALSE, now()
WHERE NOT EXISTS (SELECT 1 FROM publication WHERE title = 'PostgreSQL — Методическое пособие');

--changeset Diplom_Backend:2026-06-01-28-pub-pg-manual-link splitStatements:true endDelimiter:;
INSERT INTO subject_topic_publication (publication_id, subject_topic_id)
SELECT p.id, t.id
FROM publication p, subject_topic t
                        JOIN subject s ON t.subject_id = s.id
                        JOIN direction d ON s.direction_id = d.id
WHERE p.title = 'PostgreSQL — Методическое пособие'
  AND t.name = 'PostgreSQL'
  AND s.name = 'Базы данных'
  AND d.name = 'Учебные материалы'
  AND NOT EXISTS (
    SELECT 1 FROM subject_topic_publication stp
    WHERE stp.publication_id = p.id AND stp.subject_topic_id = t.id
);

--changeset Diplom_Backend:2026-06-01-29-pub-pg-slides splitStatements:true endDelimiter:;
INSERT INTO publication (title, description, account_id, supports_thread, created_at)
SELECT 'PostgreSQL — Презентация',
       'Презентация по СУБД PostgreSQL. Ссылка: https://disk.yandex.ru/i/TvbqlwUOzGRa7g',
       1, FALSE, now()
WHERE NOT EXISTS (SELECT 1 FROM publication WHERE title = 'PostgreSQL — Презентация');

--changeset Diplom_Backend:2026-06-01-30-pub-pg-slides-link splitStatements:true endDelimiter:;
INSERT INTO subject_topic_publication (publication_id, subject_topic_id)
SELECT p.id, t.id
FROM publication p, subject_topic t
                        JOIN subject s ON t.subject_id = s.id
                        JOIN direction d ON s.direction_id = d.id
WHERE p.title = 'PostgreSQL — Презентация'
  AND t.name = 'PostgreSQL'
  AND s.name = 'Базы данных'
  AND d.name = 'Учебные материалы'
  AND NOT EXISTS (
    SELECT 1 FROM subject_topic_publication stp
    WHERE stp.publication_id = p.id AND stp.subject_topic_id = t.id
);

-- WPF C#
--changeset Diplom_Backend:2026-06-01-31-pub-wpf-1 splitStatements:true endDelimiter:;
INSERT INTO publication (title, description, account_id, supports_thread, created_at)
SELECT 'WPF C# — Методическое пособие 1',
       'Методическое пособие по WPF C# (часть 1). Ссылка: https://disk.yandex.ru/i/tZiXizqMjv--LA',
       1, FALSE, now()
WHERE NOT EXISTS (SELECT 1 FROM publication WHERE title = 'WPF C# — Методическое пособие 1');

--changeset Diplom_Backend:2026-06-01-32-pub-wpf-1-link splitStatements:true endDelimiter:;
INSERT INTO subject_topic_publication (publication_id, subject_topic_id)
SELECT p.id, t.id
FROM publication p, subject_topic t
                        JOIN subject s ON t.subject_id = s.id
                        JOIN direction d ON s.direction_id = d.id
WHERE p.title = 'WPF C# — Методическое пособие 1'
  AND t.name = 'WPF C#'
  AND s.name = 'Desktop-разработка'
  AND d.name = 'Учебные материалы'
  AND NOT EXISTS (
    SELECT 1 FROM subject_topic_publication stp
    WHERE stp.publication_id = p.id AND stp.subject_topic_id = t.id
);

--changeset Diplom_Backend:2026-06-01-33-pub-wpf-2 splitStatements:true endDelimiter:;
INSERT INTO publication (title, description, account_id, supports_thread, created_at)
SELECT 'WPF C# — Методическое пособие 2',
       'Методическое пособие по WPF C# (часть 2). Ссылка: https://disk.yandex.ru/i/FcsjIvBXUMJyBA',
       1, FALSE, now()
WHERE NOT EXISTS (SELECT 1 FROM publication WHERE title = 'WPF C# — Методическое пособие 2');

--changeset Diplom_Backend:2026-06-01-34-pub-wpf-2-link splitStatements:true endDelimiter:;
INSERT INTO subject_topic_publication (publication_id, subject_topic_id)
SELECT p.id, t.id
FROM publication p, subject_topic t
                        JOIN subject s ON t.subject_id = s.id
                        JOIN direction d ON s.direction_id = d.id
WHERE p.title = 'WPF C# — Методическое пособие 2'
  AND t.name = 'WPF C#'
  AND s.name = 'Desktop-разработка'
  AND d.name = 'Учебные материалы'
  AND NOT EXISTS (
    SELECT 1 FROM subject_topic_publication stp
    WHERE stp.publication_id = p.id AND stp.subject_topic_id = t.id
);

--changeset Diplom_Backend:2026-06-01-35-pub-wpf-3 splitStatements:true endDelimiter:;
INSERT INTO publication (title, description, account_id, supports_thread, created_at)
SELECT 'WPF C# — Методическое пособие 3',
       'Методическое пособие по WPF C# (часть 3). Ссылка: https://disk.yandex.ru/i/a9R-9v0GliQmAQ',
       1, FALSE, now()
WHERE NOT EXISTS (SELECT 1 FROM publication WHERE title = 'WPF C# — Методическое пособие 3');

--changeset Diplom_Backend:2026-06-01-36-pub-wpf-3-link splitStatements:true endDelimiter:;
INSERT INTO subject_topic_publication (publication_id, subject_topic_id)
SELECT p.id, t.id
FROM publication p, subject_topic t
                        JOIN subject s ON t.subject_id = s.id
                        JOIN direction d ON s.direction_id = d.id
WHERE p.title = 'WPF C# — Методическое пособие 3'
  AND t.name = 'WPF C#'
  AND s.name = 'Desktop-разработка'
  AND d.name = 'Учебные материалы'
  AND NOT EXISTS (
    SELECT 1 FROM subject_topic_publication stp
    WHERE stp.publication_id = p.id AND stp.subject_topic_id = t.id
);

-- Figma
--changeset Diplom_Backend:2026-06-01-37-pub-figma-manual splitStatements:true endDelimiter:;
INSERT INTO publication (title, description, account_id, supports_thread, created_at)
SELECT 'Figma — Методическое пособие',
       'Методическое пособие по Figma. Ссылка: https://disk.yandex.ru/i/hmDKim2rYh7W-w',
       1, FALSE, now()
WHERE NOT EXISTS (SELECT 1 FROM publication WHERE title = 'Figma — Методическое пособие');

--changeset Diplom_Backend:2026-06-01-38-pub-figma-manual-link splitStatements:true endDelimiter:;
INSERT INTO subject_topic_publication (publication_id, subject_topic_id)
SELECT p.id, t.id
FROM publication p, subject_topic t
                        JOIN subject s ON t.subject_id = s.id
                        JOIN direction d ON s.direction_id = d.id
WHERE p.title = 'Figma — Методическое пособие'
  AND t.name = 'Figma'
  AND s.name = 'Инструменты разработки'
  AND d.name = 'Учебные материалы'
  AND NOT EXISTS (
    SELECT 1 FROM subject_topic_publication stp
    WHERE stp.publication_id = p.id AND stp.subject_topic_id = t.id
);

-- Django
--changeset Diplom_Backend:2026-06-01-39-pub-django-manual splitStatements:true endDelimiter:;
INSERT INTO publication (title, description, account_id, supports_thread, created_at)
SELECT 'Django — Методическое пособие',
       'Методическое пособие по фреймворку Django. Ссылка: https://disk.yandex.ru/i/wPRoF_1I2tvtuQ',
       1, FALSE, now()
WHERE NOT EXISTS (SELECT 1 FROM publication WHERE title = 'Django — Методическое пособие');

--changeset Diplom_Backend:2026-06-01-40-pub-django-manual-link splitStatements:true endDelimiter:;
INSERT INTO subject_topic_publication (publication_id, subject_topic_id)
SELECT p.id, t.id
FROM publication p, subject_topic t
                        JOIN subject s ON t.subject_id = s.id
                        JOIN direction d ON s.direction_id = d.id
WHERE p.title = 'Django — Методическое пособие'
  AND t.name = 'Django'
  AND s.name = 'Веб-разработка'
  AND d.name = 'Учебные материалы'
  AND NOT EXISTS (
    SELECT 1 FROM subject_topic_publication stp
    WHERE stp.publication_id = p.id AND stp.subject_topic_id = t.id
);

-- HTML, CSS, JavaScript
--changeset Diplom_Backend:2026-06-01-41-pub-html-manual splitStatements:true endDelimiter:;
INSERT INTO publication (title, description, account_id, supports_thread, created_at)
SELECT 'HTML, CSS, JavaScript — Методическое пособие',
       'Методическое пособие по HTML, CSS и JavaScript. Ссылка: https://disk.yandex.ru/i/LFo9QCr6q3AtBg',
       1, FALSE, now()
WHERE NOT EXISTS (SELECT 1 FROM publication WHERE title = 'HTML, CSS, JavaScript — Методическое пособие');

--changeset Diplom_Backend:2026-06-01-42-pub-html-manual-link splitStatements:true endDelimiter:;
INSERT INTO subject_topic_publication (publication_id, subject_topic_id)
SELECT p.id, t.id
FROM publication p, subject_topic t
                        JOIN subject s ON t.subject_id = s.id
                        JOIN direction d ON s.direction_id = d.id
WHERE p.title = 'HTML, CSS, JavaScript — Методическое пособие'
  AND t.name = 'HTML, CSS, JavaScript'
  AND s.name = 'Веб-разработка'
  AND d.name = 'Учебные материалы'
  AND NOT EXISTS (
    SELECT 1 FROM subject_topic_publication stp
    WHERE stp.publication_id = p.id AND stp.subject_topic_id = t.id
);

--changeset Diplom_Backend:2026-06-01-43-pub-html-slides splitStatements:true endDelimiter:;
INSERT INTO publication (title, description, account_id, supports_thread, created_at)
SELECT 'HTML, CSS, JavaScript — Презентация',
       'Презентация по HTML, CSS и JavaScript. Ссылка: https://disk.yandex.ru/i/QBUCgT7vsbqiPw',
       1, FALSE, now()
WHERE NOT EXISTS (SELECT 1 FROM publication WHERE title = 'HTML, CSS, JavaScript — Презентация');

--changeset Diplom_Backend:2026-06-01-44-pub-html-slides-link splitStatements:true endDelimiter:;
INSERT INTO subject_topic_publication (publication_id, subject_topic_id)
SELECT p.id, t.id
FROM publication p, subject_topic t
                        JOIN subject s ON t.subject_id = s.id
                        JOIN direction d ON s.direction_id = d.id
WHERE p.title = 'HTML, CSS, JavaScript — Презентация'
  AND t.name = 'HTML, CSS, JavaScript'
  AND s.name = 'Веб-разработка'
  AND d.name = 'Учебные материалы'
  AND NOT EXISTS (
    SELECT 1 FROM subject_topic_publication stp
    WHERE stp.publication_id = p.id AND stp.subject_topic_id = t.id
);

-- Bootstrap
--changeset Diplom_Backend:2026-06-01-45-pub-bootstrap-manual splitStatements:true endDelimiter:;
INSERT INTO publication (title, description, account_id, supports_thread, created_at)
SELECT 'Bootstrap — Методическое пособие',
       'Методическое пособие по Bootstrap. Ссылка: https://disk.yandex.by/i/eVpKx6WnUbPv0w',
       1, FALSE, now()
WHERE NOT EXISTS (SELECT 1 FROM publication WHERE title = 'Bootstrap — Методическое пособие');

--changeset Diplom_Backend:2026-06-01-46-pub-bootstrap-manual-link splitStatements:true endDelimiter:;
INSERT INTO subject_topic_publication (publication_id, subject_topic_id)
SELECT p.id, t.id
FROM publication p, subject_topic t
                        JOIN subject s ON t.subject_id = s.id
                        JOIN direction d ON s.direction_id = d.id
WHERE p.title = 'Bootstrap — Методическое пособие'
  AND t.name = 'Bootstrap'
  AND s.name = 'Веб-разработка'
  AND d.name = 'Учебные материалы'
  AND NOT EXISTS (
    SELECT 1 FROM subject_topic_publication stp
    WHERE stp.publication_id = p.id AND stp.subject_topic_id = t.id
);

-- Чек-листы и шаблоны
--changeset Diplom_Backend:2026-06-01-47-pub-checklist splitStatements:true endDelimiter:;
INSERT INTO publication (title, description, account_id, supports_thread, created_at)
SELECT 'Чек-лист по составлению методических пособий и презентаций',
       'Чек-лист для проверки качества методических пособий и презентаций. Ссылка: https://disk.yandex.ru/i/8N08WSwHeYbsJA',
       1, FALSE, now()
WHERE NOT EXISTS (SELECT 1 FROM publication WHERE title = 'Чек-лист по составлению методических пособий и презентаций');

--changeset Diplom_Backend:2026-06-01-48-pub-checklist-link splitStatements:true endDelimiter:;
INSERT INTO subject_topic_publication (publication_id, subject_topic_id)
SELECT p.id, t.id
FROM publication p, subject_topic t
                        JOIN subject s ON t.subject_id = s.id
                        JOIN direction d ON s.direction_id = d.id
WHERE p.title = 'Чек-лист по составлению методических пособий и презентаций'
  AND t.name = 'Чек-листы и шаблоны'
  AND s.name = 'Методические материалы'
  AND d.name = 'Учебные материалы'
  AND NOT EXISTS (
    SELECT 1 FROM subject_topic_publication stp
    WHERE stp.publication_id = p.id AND stp.subject_topic_id = t.id
);

--changeset Diplom_Backend:2026-06-01-49-pub-template splitStatements:true endDelimiter:;
INSERT INTO publication (title, description, account_id, supports_thread, created_at)
SELECT 'Шаблон по составлению презентаций',
       'Шаблон для оформления презентаций (МосПолитех). Ссылка: https://mospolytech.ru/ob-universitete/brandbook/?sphrase_id=1694184',
       1, FALSE, now()
WHERE NOT EXISTS (SELECT 1 FROM publication WHERE title = 'Шаблон по составлению презентаций');

--changeset Diplom_Backend:2026-06-01-50-pub-template-link splitStatements:true endDelimiter:;
INSERT INTO subject_topic_publication (publication_id, subject_topic_id)
SELECT p.id, t.id
FROM publication p, subject_topic t
                        JOIN subject s ON t.subject_id = s.id
                        JOIN direction d ON s.direction_id = d.id
WHERE p.title = 'Шаблон по составлению презентаций'
  AND t.name = 'Чек-листы и шаблоны'
  AND s.name = 'Методические материалы'
  AND d.name = 'Учебные материалы'
  AND NOT EXISTS (
    SELECT 1 FROM subject_topic_publication stp
    WHERE stp.publication_id = p.id AND stp.subject_topic_id = t.id
);