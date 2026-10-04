package org.diplom_backend.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SubmissionStatus {
    SUBMITTED("Отправлено"),
    ACCEPTED("Принято"),
    NEEDS_REVISION("На доработку");

    private final String description;
}
