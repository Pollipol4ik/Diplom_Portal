package org.diplom_backend.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SubmissionType {
    TEXT("Текстовый ответ"),
    FILE("Загрузка файла"),
    TEXT_AND_FILE("Текст и файл");

    private final String description;
}
