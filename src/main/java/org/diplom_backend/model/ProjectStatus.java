package org.diplom_backend.model;

public enum ProjectStatus {
    ON_REVIEW("На проверке"),
    NEEDS_REVISION("На доработку"),
    ACCEPTED("Принято"),
    REJECTED("Отклонено");

    private final String description;

    ProjectStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}