-- ── Вводный курс: Основы проектной деятельности ──────────────────────────────

--changeset Diplom_Backend:2026-06-04-01-intro-course-basics splitStatements:true endDelimiter:;
INSERT INTO course (name, description, is_active, for_lagging_students, is_introduction, created_at)
SELECT
    'Основы проектной деятельности',
    'Вводный курс для всех учеников ИТ-классов. Знакомит с этапами проектной деятельности, требованиями к проектным работам, правилами работы в группе и инструментами платформы. Обязателен к прохождению перед выбором целевого курса.',
    TRUE, FALSE, TRUE, now()
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Основы проектной деятельности');

--changeset Diplom_Backend:2026-06-04-02-intro-basics-lessons splitStatements:true endDelimiter:;
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_open_for_students, created_at)
SELECT c.id,
       'Что такое проектная деятельность',
       'Проектная деятельность — это форма обучения, при которой ученики самостоятельно решают реальные задачи под руководством наставника. Основные принципы: актуальность, практическая значимость, командная работа, публичная защита результатов. Структура проекта: идея → анализ → проектирование → реализация → защита.',
       'Прочитайте материал и ответьте на вопрос: какую проблему вы хотели бы решить с помощью ИТ-проекта? Опишите идею в свободной форме (3–5 предложений).',
       'TEXT', 1, 10, 'LESSON', TRUE, now()
FROM course c WHERE c.name = 'Основы проектной деятельности'
                AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 1);

--changeset Diplom_Backend:2026-06-04-03-intro-basics-lesson2 splitStatements:true endDelimiter:;
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_open_for_students, created_at)
SELECT c.id,
       'Обзор направлений проектной деятельности',
       'В рамках ИТ-классов МосПолитех реализуются следующие направления: 3D-моделирование и VR/AR, инновации умного города, киберфизические системы, мобильная разработка и игры, программирование и веб-разработка, бизнес-приложения, сетевые технологии, цифровые технологии в социокультурной сфере. Каждое направление предполагает создание оригинального проекта за учебный год.',
       'Изучите описания всех восьми направлений и выберите три наиболее интересных для вас. Обоснуйте свой выбор (по 2–3 предложения на каждое направление).',
       'TEXT', 2, 10, 'LESSON', TRUE, now()
FROM course c WHERE c.name = 'Основы проектной деятельности'
                AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 2);

--changeset Diplom_Backend:2026-06-04-04-intro-basics-lesson3 splitStatements:true endDelimiter:;
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_open_for_students, created_at)
SELECT c.id,
       'Требования к проекту и критерии оценивания',
       'Каждый проект оценивается по нескольким критериям: актуальность и новизна идеи, качество реализации, оформление пояснительной записки, качество презентации, ответы на вопросы комиссии. Итоговая оценка складывается из оценок за каждый этап курса. Обязательные этапы: согласование темы, промежуточный показ, финальная защита.',
       'Ознакомьтесь с чек-листом требований к проекту (раздел «Учебные материалы → Методические материалы»). Составьте план: какие этапы вам предстоит пройти и какие материалы нужно подготовить на каждом из них.',
       'TEXT', 3, 10, 'LESSON', TRUE, now()
FROM course c WHERE c.name = 'Основы проектной деятельности'
                AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 3);

--changeset Diplom_Backend:2026-06-04-05-intro-basics-lesson4 splitStatements:true endDelimiter:;
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_open_for_students, created_at)
SELECT c.id,
       'Работа в команде и выбор темы',
       'Проектная группа формируется из учеников одной школы. Рекомендуемый состав: 2–4 человека. Тема проекта должна быть оригинальной и не совпадать с темами других групп курса. При выборе темы используйте банк идей платформы — в нём собраны успешные темы прошлых лет с комментариями наставников.',
       'Определитесь с составом вашей группы (или подтвердите работу в одиночку). Изучите банк идей и выберите предварительную тему. Опишите выбранную тему и состав группы.',
       'TEXT', 4, 10, 'LESSON', TRUE, now()
FROM course c WHERE c.name = 'Основы проектной деятельности'
                AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 4);

-- ── Вводный курс: Введение в разработку ПО ───────────────────────────────────

--changeset Diplom_Backend:2026-06-04-06-intro-course-dev splitStatements:true endDelimiter:;
INSERT INTO course (name, description, is_active, for_lagging_students, is_introduction, created_at)
SELECT
    'Введение в разработку программного обеспечения',
    'Вводный курс для учеников, ещё не определившихся с направлением. Охватывает базовые концепции разработки ПО: жизненный цикл, инструменты, системы контроля версий, командная работа. Помогает сориентироваться перед выбором целевого курса.',
    TRUE, FALSE, TRUE, now()
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Введение в разработку программного обеспечения');

--changeset Diplom_Backend:2026-06-04-07-intro-dev-lesson1 splitStatements:true endDelimiter:;
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_open_for_students, created_at)
SELECT c.id,
       'Жизненный цикл разработки ПО',
       'Жизненный цикл программного обеспечения (SDLC) включает этапы: анализ требований, проектирование, разработка, тестирование, развёртывание, поддержка. Популярные методологии: Waterfall (каскадная) и Agile (гибкая). В проектной деятельности ИТ-классов используется упрощённая версия Agile с итеративным улучшением.',
       'Изучите описание двух методологий. Опишите, какой подход лучше подходит для вашего будущего проекта и почему.',
       'TEXT', 1, 10, 'LESSON', TRUE, now()
FROM course c WHERE c.name = 'Введение в разработку программного обеспечения'
                AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 1);

--changeset Diplom_Backend:2026-06-04-08-intro-dev-lesson2 splitStatements:true endDelimiter:;
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_open_for_students, created_at)
SELECT c.id,
       'Системы контроля версий: GIT',
       'GIT — распределённая система контроля версий, позволяющая отслеживать изменения в коде и совместно работать над проектом. Основные команды: git init, git add, git commit, git push, git pull, git branch, git merge. Платформы: GitHub, GitLab, Gitea. Методическое пособие доступно в разделе «Учебные материалы → Инструменты разработки → GIT».',
       'Создайте репозиторий на GitHub или GitLab. Добавьте файл README.md с описанием вашего будущего проекта и сделайте первый коммит. Прикрепите ссылку на репозиторий.',
       'TEXT', 2, 15, 'LESSON', TRUE, now()
FROM course c WHERE c.name = 'Введение в разработку программного обеспечения'
                AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 2);

--changeset Diplom_Backend:2026-06-04-09-intro-dev-lesson3 splitStatements:true endDelimiter:;
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_open_for_students, created_at)
SELECT c.id,
       'Инструменты проектирования: Figma',
       'Figma — инструмент для создания прототипов интерфейсов и командной работы над дизайном. Позволяет создавать макеты экранов, задавать навигацию между ними и демонстрировать пользовательский сценарий без написания кода. Методическое пособие доступно в разделе «Учебные материалы → Инструменты разработки → Figma».',
       'Создайте простой прототип главного экрана вашего будущего приложения или сайта в Figma. Экспортируйте скриншот и загрузите его.',
       'FILE', 3, 15, 'LESSON', TRUE, now()
FROM course c WHERE c.name = 'Введение в разработку программного обеспечения'
                AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 3);

-- =============================================================================
-- КУРСЫ ДЛЯ ОТСТАЮЩИХ (for_lagging_students = TRUE)
-- =============================================================================

-- ── Курс для отстающих: Индивидуальная проектная работа ──────────────────────

--changeset Diplom_Backend:2026-06-04-10-lagging-course-individual splitStatements:true endDelimiter:;
INSERT INTO course (name, description, is_active, for_lagging_students, is_introduction, created_at)
SELECT
    'Индивидуальная проектная работа',
    'Курс поддержки для учеников, не выбравших тему проекта в установленный срок. Предполагает индивидуальное сопровождение модератором: выбор упрощённой темы, поэтапное выполнение с обязательной проверкой каждого шага. Цель — успешно завершить проект и пройти итоговую защиту.',
    TRUE, TRUE, FALSE, now()
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Индивидуальная проектная работа');

--changeset Diplom_Backend:2026-06-04-11-lagging-individual-lesson1 splitStatements:true endDelimiter:;
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students, created_at)
SELECT c.id,
       'Выбор и согласование темы проекта',
       'На этом этапе вам необходимо выбрать тему индивидуального проекта. Рекомендуется использовать банк идей платформы — там собраны темы, успешно реализованные в прошлых годах. Модератор поможет скорректировать тему под ваши возможности. Тема должна быть реалистичной и выполнимой в оставшееся время.',
       'Выберите тему из банка идей или предложите свою. Опишите: что вы хотите создать, какую проблему это решает, какими инструментами планируете воспользоваться.',
       'TEXT', 1, 10, 'HEARING', 'TOPIC_APPROVAL', TRUE, now()
FROM course c WHERE c.name = 'Индивидуальная проектная работа'
                AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 1);

--changeset Diplom_Backend:2026-06-04-12-lagging-individual-lesson2 splitStatements:true endDelimiter:;
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_open_for_students, created_at)
SELECT c.id,
       'Анализ предметной области',
       'Анализ предметной области позволяет понять контекст задачи и найти наилучшее решение. Включает: определение целевой аудитории, изучение аналогов, формулировку требований к продукту.',
       'Проведите анализ предметной области вашего проекта. Опишите: кто будет пользоваться вашим продуктом, какие аналоги уже существуют, чем ваш проект будет отличаться от них.',
       'TEXT_AND_FILE', 2, 10, 'LESSON', TRUE, now()
FROM course c WHERE c.name = 'Индивидуальная проектная работа'
                AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 2);

--changeset Diplom_Backend:2026-06-04-13-lagging-individual-lesson3 splitStatements:true endDelimiter:;
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_open_for_students, created_at)
SELECT c.id,
       'Разработка проекта',
       'На этом этапе выполняется основная практическая работа по созданию продукта. Загружайте промежуточные результаты по мере готовности — модератор будет давать обратную связь. Не откладывайте сдачу до последнего момента.',
       'Разработайте рабочую версию вашего проекта. Загрузите результат: исходный код, скриншоты, видеодемонстрацию или ссылку на работающий прототип.',
       'FILE', 3, 20, 'LESSON', TRUE, now()
FROM course c WHERE c.name = 'Индивидуальная проектная работа'
                AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 3);

--changeset Diplom_Backend:2026-06-04-14-lagging-individual-lesson4 splitStatements:true endDelimiter:;
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_open_for_students, created_at)
SELECT c.id,
       'Оформление пояснительной записки',
       'Пояснительная записка — обязательный документ, описывающий ваш проект. Структура: титульный лист, введение, основная часть, заключение, список источников. Шаблон и чек-лист доступны в разделе «Учебные материалы → Методические материалы».',
       'Оформите пояснительную записку к проекту согласно требованиям. Загрузите документ в формате PDF или DOCX.',
       'FILE', 4, 10, 'LESSON', TRUE, now()
FROM course c WHERE c.name = 'Индивидуальная проектная работа'
                AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 4);

--changeset Diplom_Backend:2026-06-04-15-lagging-individual-lesson5 splitStatements:true endDelimiter:;
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students, created_at)
SELECT c.id,
       'Защита проекта',
       'Итоговая защита проходит перед комиссией. Подготовьте презентацию на 7–10 слайдов: постановка задачи, предложенное решение, ключевые технические решения, результаты и выводы. Будьте готовы ответить на вопросы по реализации.',
       'Подготовьте презентацию проекта и загрузите её вместе с итоговыми материалами (пояснительная записка, исходный код или ссылка на репозиторий).',
       'FILE', 5, 30, 'HEARING', 'FINAL', FALSE, now()
FROM course c WHERE c.name = 'Индивидуальная проектная работа'
                AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 5);

-- ── Курс для отстающих: Основы программирования ──────────────────────────────

--changeset Diplom_Backend:2026-06-04-16-lagging-course-prog splitStatements:true endDelimiter:;
INSERT INTO course (name, description, is_active, for_lagging_students, is_introduction, created_at)
SELECT
    'Основы программирования для отстающих',
    'Курс поддержки для учеников с недостаточной базой по программированию. Охватывает фундаментальные концепции: переменные, условия, циклы, функции, работа с данными. Выполнение практических заданий с индивидуальной проверкой модератором.',
    TRUE, TRUE, FALSE, now()
WHERE NOT EXISTS (SELECT 1 FROM course WHERE name = 'Основы программирования для отстающих');

--changeset Diplom_Backend:2026-06-04-17-lagging-prog-lesson1 splitStatements:true endDelimiter:;
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students, created_at)
SELECT c.id,
       'Выбор темы мини-проекта',
       'В этом курсе вы создадите небольшой программный проект, закрепляющий базовые навыки. Примеры тем: калькулятор, список задач, простая игра, конвертер единиц, викторина. Тема должна быть небольшой и реалистичной — главное, чтобы вы самостоятельно реализовали её от начала до конца.',
       'Выберите тему мини-проекта и опишите, что именно вы планируете создать и на каком языке программирования.',
       'TEXT', 1, 10, 'HEARING', 'TOPIC_APPROVAL', TRUE, now()
FROM course c WHERE c.name = 'Основы программирования для отстающих'
                AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 1);

--changeset Diplom_Backend:2026-06-04-18-lagging-prog-lesson2 splitStatements:true endDelimiter:;
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_open_for_students, created_at)
SELECT c.id,
       'Переменные, типы данных, условия',
       'Переменная — именованная область памяти для хранения данных. Основные типы: целое число (int), число с плавающей точкой (float), строка (string), логическое значение (bool). Условный оператор if/else позволяет выполнять разные действия в зависимости от условия.',
       'Напишите программу, которая принимает от пользователя число и определяет: положительное оно, отрицательное или ноль. Загрузите исходный код.',
       'FILE', 2, 15, 'LESSON', TRUE, now()
FROM course c WHERE c.name = 'Основы программирования для отстающих'
                AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 2);

--changeset Diplom_Backend:2026-06-04-19-lagging-prog-lesson3 splitStatements:true endDelimiter:;
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_open_for_students, created_at)
SELECT c.id,
       'Циклы и функции',
       'Цикл позволяет повторять блок кода заданное количество раз (for) или пока выполняется условие (while). Функция — именованный блок кода, который можно вызывать многократно. Использование функций делает код читаемым и повторно используемым.',
       'Напишите программу с использованием цикла и минимум одной функцией. Например: вычисление факториала, таблица умножения или сортировка списка. Загрузите исходный код.',
       'FILE', 3, 15, 'LESSON', TRUE, now()
FROM course c WHERE c.name = 'Основы программирования для отстающих'
                AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 3);

--changeset Diplom_Backend:2026-06-04-20-lagging-prog-lesson4 splitStatements:true endDelimiter:;
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_open_for_students, created_at)
SELECT c.id,
       'Реализация мини-проекта',
       'На этом этапе вы создаёте полноценную рабочую программу по выбранной теме. Применяйте всё, что изучили: переменные, условия, циклы, функции. Код должен быть структурирован, снабжён комментариями и корректно обрабатывать пользовательский ввод.',
       'Реализуйте ваш мини-проект полностью. Загрузите исходный код и краткую инструкцию по запуску.',
       'FILE', 4, 20, 'LESSON', TRUE, now()
FROM course c WHERE c.name = 'Основы программирования для отстающих'
                AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 4);

--changeset Diplom_Backend:2026-06-04-21-lagging-prog-lesson5 splitStatements:true endDelimiter:;
INSERT INTO course_lesson (course_id, title, lecture_content, practice_description, submission_type, order_number, max_score, category, hearing_stage, hearing_open_for_students, created_at)
SELECT c.id,
       'Презентация и защита мини-проекта',
       'Подготовьте краткую презентацию вашего мини-проекта: что создали, как работает, какие трудности возникли и как вы их решили. Демонстрация работающей программы обязательна.',
       'Загрузите презентацию (5–7 слайдов) и финальную версию исходного кода. Будьте готовы продемонстрировать работу программы.',
       'FILE', 5, 30, 'HEARING', 'FINAL', FALSE, now()
FROM course c WHERE c.name = 'Основы программирования для отстающих'
                AND NOT EXISTS (SELECT 1 FROM course_lesson cl WHERE cl.course_id = c.id AND cl.order_number = 5);