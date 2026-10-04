--liquibase formatted sql

-- =============================================================================
-- Начальные данные банка идей: архивные темы проектов по курсам ИТ-классов.
-- Все INSERT используют WHERE NOT EXISTS для идемпотентности.
-- created_by_id = 1 — системный аккаунт администратора.
-- =============================================================================

-- ── 3D-моделирование, 3D-печать и VR/AR-технологии ──────────────────────────

--changeset Diplom_Backend:2026-06-02-01-idea-3d-1 splitStatements:true endDelimiter:;
INSERT INTO idea_bank (title, description, comments, score, created_by_id, course_id, created_at)
SELECT
    'VR-тренажёр по пожарной безопасности',
    'Разработка виртуального тренажёра для обучения школьников правилам пожарной безопасности с использованием VR-гарнитуры. Включает сценарий эвакуации и тест по итогам.',
    'Хорошая проработка сценария. Рекомендовано добавить оценку времени реакции пользователя.',
    9,
    1,
    c.id,
    now()
FROM course c
WHERE c.name = '3D-моделирование, 3D-печать и VR/AR-технологии'
  AND NOT EXISTS (SELECT 1 FROM idea_bank ib WHERE ib.title = 'VR-тренажёр по пожарной безопасности');

--changeset Diplom_Backend:2026-06-02-02-idea-3d-2 splitStatements:true endDelimiter:;
INSERT INTO idea_bank (title, description, comments, score, created_by_id, course_id, created_at)
SELECT
    'Макет умного города с 3D-печатью',
    'Создание физического макета фрагмента города с использованием 3D-принтера: здания, дороги, элементы инфраструктуры. Сопровождается AR-слоем с информацией об объектах.',
    'Интересная идея совмещения физического и цифрового пространства. Проработать масштабирование.',
    8,
    1,
    c.id,
    now()
FROM course c
WHERE c.name = '3D-моделирование, 3D-печать и VR/AR-технологии'
  AND NOT EXISTS (SELECT 1 FROM idea_bank ib WHERE ib.title = 'Макет умного города с 3D-печатью');

--changeset Diplom_Backend:2026-06-02-03-idea-3d-3 splitStatements:true endDelimiter:;
INSERT INTO idea_bank (title, description, comments, score, created_by_id, course_id, created_at)
SELECT
    'AR-приложение для изучения анатомии',
    'Мобильное AR-приложение, позволяющее рассмотреть трёхмерные модели органов человека, наложенные на реальный мир через камеру смартфона. Целевая аудитория — ученики средней школы.',
    'Перспективная тема для образования. Рекомендовано добавить озвучку и интерактивные подсказки.',
    10,
    1,
    c.id,
    now()
FROM course c
WHERE c.name = '3D-моделирование, 3D-печать и VR/AR-технологии'
  AND NOT EXISTS (SELECT 1 FROM idea_bank ib WHERE ib.title = 'AR-приложение для изучения анатомии');

-- ── Инновации умного города. Умная школа ─────────────────────────────────────

--changeset Diplom_Backend:2026-06-02-04-idea-smart-1 splitStatements:true endDelimiter:;
INSERT INTO idea_bank (title, description, comments, score, created_by_id, course_id, created_at)
SELECT
    'Система мониторинга загруженности школьной столовой',
    'Веб-сервис с датчиком на входе и дашбордом в реальном времени, показывающим количество людей в столовой и предсказывающим пиковые часы на основе исторических данных.',
    'Отличная практическая применимость. Следует проработать вопрос приватности при подсчёте посетителей.',
    9,
    1,
    c.id,
    now()
FROM course c
WHERE c.name = 'Инновации умного города. Умная школа'
  AND NOT EXISTS (SELECT 1 FROM idea_bank ib WHERE ib.title = 'Система мониторинга загруженности школьной столовой');

--changeset Diplom_Backend:2026-06-02-05-idea-smart-2 splitStatements:true endDelimiter:;
INSERT INTO idea_bank (title, description, comments, score, created_by_id, course_id, created_at)
SELECT
    'Умное расписание с уведомлениями',
    'Приложение для автоматической генерации оптимального расписания уроков с учётом занятости кабинетов и учителей. Отправляет push-уведомления об изменениях.',
    'Хорошая реализация алгоритма планирования. Добавить интеграцию с существующими системами школы.',
    8,
    1,
    c.id,
    now()
FROM course c
WHERE c.name = 'Инновации умного города. Умная школа'
  AND NOT EXISTS (SELECT 1 FROM idea_bank ib WHERE ib.title = 'Умное расписание с уведомлениями');

--changeset Diplom_Backend:2026-06-02-06-idea-smart-3 splitStatements:true endDelimiter:;
INSERT INTO idea_bank (title, description, comments, score, created_by_id, course_id, created_at)
SELECT
    'Экологический мониторинг школьного района',
    'IoT-стенд с датчиками CO₂, температуры и влажности, данные с которого публикуются на публичной карте. Позволяет ученикам изучать экологическую обстановку в районе школы.',
    'Хорошая связь с темой умного города. Уточнить выбор платформы для сбора данных (MQTT/HTTP).',
    7,
    1,
    c.id,
    now()
FROM course c
WHERE c.name = 'Инновации умного города. Умная школа'
  AND NOT EXISTS (SELECT 1 FROM idea_bank ib WHERE ib.title = 'Экологический мониторинг школьного района');

-- ── Киберфизические системы ──────────────────────────────────────────────────

--changeset Diplom_Backend:2026-06-02-07-idea-cyber-1 splitStatements:true endDelimiter:;
INSERT INTO idea_bank (title, description, comments, score, created_by_id, course_id, created_at)
SELECT
    'Автономный робот-сортировщик мусора',
    'Мобильная платформа на Arduino/Raspberry Pi с камерой и классификатором на основе нейросети, определяющая тип мусора (пластик, бумага, металл) и раскладывающая его по контейнерам.',
    'Сильная техническая составляющая. Рекомендовано улучшить точность модели на нестандартных объектах.',
    10,
    1,
    c.id,
    now()
FROM course c
WHERE c.name = 'Киберфизические системы'
  AND NOT EXISTS (SELECT 1 FROM idea_bank ib WHERE ib.title = 'Автономный робот-сортировщик мусора');

--changeset Diplom_Backend:2026-06-02-08-idea-cyber-2 splitStatements:true endDelimiter:;
INSERT INTO idea_bank (title, description, comments, score, created_by_id, course_id, created_at)
SELECT
    'Система умного полива растений',
    'Автоматизированная система полива на базе микроконтроллера с датчиками влажности почвы и температуры воздуха. Управление через мобильное приложение с графиками состояния.',
    'Хорошая проработка схемотехники. Добавить режим ручного управления и журнал событий.',
    8,
    1,
    c.id,
    now()
FROM course c
WHERE c.name = 'Киберфизические системы'
  AND NOT EXISTS (SELECT 1 FROM idea_bank ib WHERE ib.title = 'Система умного полива растений');

-- ── Мобильная разработка и разработка игр ───────────────────────────────────

--changeset Diplom_Backend:2026-06-02-09-idea-mobile-1 splitStatements:true endDelimiter:;
INSERT INTO idea_bank (title, description, comments, score, created_by_id, course_id, created_at)
SELECT
    'Мобильное приложение для изучения иностранных слов',
    'Android-приложение с карточками, системой интервального повторения (алгоритм SuperMemo) и статистикой прогресса. Поддерживает создание пользовательских колод.',
    'Алгоритм повторения реализован корректно. Рекомендовано добавить звуковое произношение слов.',
    9,
    1,
    c.id,
    now()
FROM course c
WHERE c.name = 'Мобильная разработка и разработка игр'
  AND NOT EXISTS (SELECT 1 FROM idea_bank ib WHERE ib.title = 'Мобильное приложение для изучения иностранных слов');

--changeset Diplom_Backend:2026-06-02-10-idea-mobile-2 splitStatements:true endDelimiter:;
INSERT INTO idea_bank (title, description, comments, score, created_by_id, course_id, created_at)
SELECT
    '2D-платформер с процедурной генерацией уровней',
    'Игра на Unity с автоматической генерацией уровней на основе алгоритма BSP (Binary Space Partitioning). Включает систему прогрессии персонажа и таблицу рекордов.',
    'Хорошая реализация BSP. Рекомендовано добавить звуковое сопровождение и более разнообразных врагов.',
    8,
    1,
    c.id,
    now()
FROM course c
WHERE c.name = 'Мобильная разработка и разработка игр'
  AND NOT EXISTS (SELECT 1 FROM idea_bank ib WHERE ib.title = '2D-платформер с процедурной генерацией уровней');

--changeset Diplom_Backend:2026-06-02-11-idea-mobile-3 splitStatements:true endDelimiter:;
INSERT INTO idea_bank (title, description, comments, score, created_by_id, course_id, created_at)
SELECT
    'Трекер привычек с геймификацией',
    'Мобильное приложение для формирования полезных привычек: ежедневные задачи, система уровней и достижений, напоминания. Хранит историю выполнения и строит графики.',
    'Механика геймификации продумана хорошо. Добавить социальные функции — соревнование с друзьями.',
    7,
    1,
    c.id,
    now()
FROM course c
WHERE c.name = 'Мобильная разработка и разработка игр'
  AND NOT EXISTS (SELECT 1 FROM idea_bank ib WHERE ib.title = 'Трекер привычек с геймификацией');

-- ── Программирование. Разработка программ, приложений, веб-сайтов ───────────

--changeset Diplom_Backend:2026-06-02-12-idea-prog-1 splitStatements:true endDelimiter:;
INSERT INTO idea_bank (title, description, comments, score, created_by_id, course_id, created_at)
SELECT
    'Платформа для обмена учебными материалами между школами',
    'Веб-приложение, где учителя и ученики публикуют конспекты, задачи и презентации с тегами по предметам и классам. Включает рейтинг материалов и систему комментариев.',
    'Актуальная тема. Проработать модерацию контента и защиту авторских прав.',
    9,
    1,
    c.id,
    now()
FROM course c
WHERE c.name = 'Программирование. Разработка программ, приложений, веб-сайтов'
  AND NOT EXISTS (SELECT 1 FROM idea_bank ib WHERE ib.title = 'Платформа для обмена учебными материалами между школами');

--changeset Diplom_Backend:2026-06-02-13-idea-prog-2 splitStatements:true endDelimiter:;
INSERT INTO idea_bank (title, description, comments, score, created_by_id, course_id, created_at)
SELECT
    'Генератор тестов по школьной программе',
    'Веб-сервис, позволяющий учителям создавать тесты с вопросами разных типов (одиночный выбор, множественный, текстовый ввод). Автоматическая проверка и выгрузка результатов в Excel.',
    'Хорошая практическая применимость. Добавить банк готовых вопросов с возможностью случайной выборки.',
    8,
    1,
    c.id,
    now()
FROM course c
WHERE c.name = 'Программирование. Разработка программ, приложений, веб-сайтов'
  AND NOT EXISTS (SELECT 1 FROM idea_bank ib WHERE ib.title = 'Генератор тестов по школьной программе');

--changeset Diplom_Backend:2026-06-02-14-idea-prog-3 splitStatements:true endDelimiter:;
INSERT INTO idea_bank (title, description, comments, score, created_by_id, course_id, created_at)
SELECT
    'Сервис визуализации алгоритмов сортировки',
    'Интерактивный веб-сайт с пошаговой анимацией популярных алгоритмов сортировки (пузырьковая, быстрая, сортировка слиянием). Поддерживает ввод произвольного массива и регулировку скорости.',
    'Отличный образовательный инструмент. Рекомендовано добавить сравнительную статистику по числу операций.',
    10,
    1,
    c.id,
    now()
FROM course c
WHERE c.name = 'Программирование. Разработка программ, приложений, веб-сайтов'
  AND NOT EXISTS (SELECT 1 FROM idea_bank ib WHERE ib.title = 'Сервис визуализации алгоритмов сортировки');

-- ── Разработка бизнес приложений ─────────────────────────────────────────────

--changeset Diplom_Backend:2026-06-02-15-idea-biz-1 splitStatements:true endDelimiter:;
INSERT INTO idea_bank (title, description, comments, score, created_by_id, course_id, created_at)
SELECT
    'CRM-система для малого бизнеса',
    'Десктопное приложение на WPF для ведения клиентской базы, учёта заказов и формирования отчётов. Включает напоминания о задачах и экспорт данных в PDF.',
    'Хорошая проработка бизнес-логики. Рекомендовано добавить роли пользователей (менеджер/администратор).',
    9,
    1,
    c.id,
    now()
FROM course c
WHERE c.name = 'Разработка бизнес приложений'
  AND NOT EXISTS (SELECT 1 FROM idea_bank ib WHERE ib.title = 'CRM-система для малого бизнеса');

--changeset Diplom_Backend:2026-06-02-16-idea-biz-2 splitStatements:true endDelimiter:;
INSERT INTO idea_bank (title, description, comments, score, created_by_id, course_id, created_at)
SELECT
    'Система учёта складских остатков',
    'Приложение для автоматизации складского учёта: приход/расход товаров, инвентаризация, уведомление о критическом остатке. Формирование отчётов по периодам.',
    'Практически применимая система. Добавить поддержку штрихкодов и импорт из Excel.',
    8,
    1,
    c.id,
    now()
FROM course c
WHERE c.name = 'Разработка бизнес приложений'
  AND NOT EXISTS (SELECT 1 FROM idea_bank ib WHERE ib.title = 'Система учёта складских остатков');

-- ── Сетевые технологии ───────────────────────────────────────────────────────

--changeset Diplom_Backend:2026-06-02-17-idea-net-1 splitStatements:true endDelimiter:;
INSERT INTO idea_bank (title, description, comments, score, created_by_id, course_id, created_at)
SELECT
    'Мониторинг доступности сервисов школьной сети',
    'Утилита для периодической проверки доступности внутренних серверов и веб-сервисов школы. Ведёт лог недоступности и отправляет оповещения администратору по email.',
    'Хорошая практическая задача. Рекомендовано добавить веб-дашборд с историей доступности.',
    8,
    1,
    c.id,
    now()
FROM course c
WHERE c.name = 'Сетевые технологии'
  AND NOT EXISTS (SELECT 1 FROM idea_bank ib WHERE ib.title = 'Мониторинг доступности сервисов школьной сети');

--changeset Diplom_Backend:2026-06-02-18-idea-net-2 splitStatements:true endDelimiter:;
INSERT INTO idea_bank (title, description, comments, score, created_by_id, course_id, created_at)
SELECT
    'Чат-мессенджер в локальной сети',
    'Клиент-серверное приложение для обмена сообщениями внутри локальной школьной сети. Поддерживает групповые чаты, передачу файлов и шифрование трафика.',
    'Хорошая реализация протокола. Проработать аутентификацию и защиту от перехвата сообщений.',
    9,
    1,
    c.id,
    now()
FROM course c
WHERE c.name = 'Сетевые технологии'
  AND NOT EXISTS (SELECT 1 FROM idea_bank ib WHERE ib.title = 'Чат-мессенджер в локальной сети');

-- ── Цифровые технологии в социокультурной сфере ─────────────────────────────

--changeset Diplom_Backend:2026-06-02-19-idea-socio-1 splitStatements:true endDelimiter:;
INSERT INTO idea_bank (title, description, comments, score, created_by_id, course_id, created_at)
SELECT
    'Интерактивная карта культурных мест района',
    'Веб-приложение с картой, на которой ученики отмечают и описывают культурные объекты своего района: памятники, галереи, исторические здания. Включает систему отзывов и фотогалерею.',
    'Хорошая краеведческая направленность. Добавить возможность создания тематических маршрутов.',
    8,
    1,
    c.id,
    now()
FROM course c
WHERE c.name = 'Цифровые технологии в социокультурной сфере'
  AND NOT EXISTS (SELECT 1 FROM idea_bank ib WHERE ib.title = 'Интерактивная карта культурных мест района');

--changeset Diplom_Backend:2026-06-02-20-idea-socio-2 splitStatements:true endDelimiter:;
INSERT INTO idea_bank (title, description, comments, score, created_by_id, course_id, created_at)
SELECT
    'Цифровой архив школьной истории',
    'Сайт для хранения и публикации фотографий, документов и воспоминаний выпускников разных лет. Хронологическая лента, поиск по людям и событиям.',
    'Ценная идея для сохранения школьной памяти. Проработать вопрос прав на публикацию личных данных.',
    9,
    1,
    c.id,
    now()
FROM course c
WHERE c.name = 'Цифровые технологии в социокультурной сфере'
  AND NOT EXISTS (SELECT 1 FROM idea_bank ib WHERE ib.title = 'Цифровой архив школьной истории');

--changeset Diplom_Backend:2026-06-02-21-idea-socio-3 splitStatements:true endDelimiter:;
INSERT INTO idea_bank (title, description, comments, score, created_by_id, course_id, created_at)
SELECT
    'Платформа для школьных мероприятий',
    'Веб-сервис для анонсирования и регистрации на школьные события: концерты, олимпиады, конкурсы. Уведомления по email, личный кабинет участника, статистика посещаемости.',
    'Актуальная тема. Рекомендовано добавить фотоотчёты и форму обратной связи после мероприятия.',
    8,
    1,
    c.id,
    now()
FROM course c
WHERE c.name = 'Цифровые технологии в социокультурной сфере'
  AND NOT EXISTS (SELECT 1 FROM idea_bank ib WHERE ib.title = 'Платформа для школьных мероприятий');