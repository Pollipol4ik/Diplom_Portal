package org.diplom_backend.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SubmissionReviewHistoryKind {
    MODERATOR("Проверка"),
    STUDENT_REPLY("Ответ ученика");

    private final String description;
}
