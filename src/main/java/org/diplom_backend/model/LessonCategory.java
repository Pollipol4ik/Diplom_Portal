package org.diplom_backend.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LessonCategory {
    LESSON("Урок"),
    HEARING("Слушание");

    private final String description;
}
