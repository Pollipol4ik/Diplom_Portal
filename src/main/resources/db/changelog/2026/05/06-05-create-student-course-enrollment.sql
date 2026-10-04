--changeset Diplom_Backend:2026-06-05-01
CREATE TABLE IF NOT EXISTS student_course_enrollment (
                                           id BIGSERIAL PRIMARY KEY,
                                           student_id BIGINT NOT NULL,
                                           course_id BIGINT NOT NULL,
                                           assigned_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                           assigned_by BIGINT,
                                           created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                           updated_at TIMESTAMP NOT NULL DEFAULT NOW(),

                                           CONSTRAINT fk_enrollment_student FOREIGN KEY (student_id)
                                               REFERENCES account(id) ON DELETE CASCADE,
                                           CONSTRAINT fk_enrollment_course FOREIGN KEY (course_id)
                                               REFERENCES course(id) ON DELETE CASCADE,
                                           CONSTRAINT fk_enrollment_assigned_by FOREIGN KEY (assigned_by)
                                               REFERENCES account(id) ON DELETE SET NULL,
                                           CONSTRAINT uk_student_course UNIQUE (student_id, course_id)
);

-- 2. Индексы для быстрого поиска
CREATE INDEX idx_enrollment_student_id ON student_course_enrollment(student_id);
CREATE INDEX idx_enrollment_course_id ON student_course_enrollment(course_id);
CREATE INDEX idx_enrollment_assigned_at ON student_course_enrollment(assigned_at);