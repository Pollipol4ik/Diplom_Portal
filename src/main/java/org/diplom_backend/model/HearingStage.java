package org.diplom_backend.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HearingStage {
    TOPIC_APPROVAL("Выбор и согласование темы"),
    INTERMEDIATE("Промежуточный показ"),
    FINAL("Финальный показ"),
    /** Итоговая защита перед комиссией на конференции; оценивается по нескольким критериям */
    CONFERENCE_DEFENSE("Защита на конференции");

    private final String description;

    public int getNumber() {
        return this.ordinal() + 1;
    }

    public static HearingStage fromNumber(int number) {
        for (HearingStage stage : values()) {
            if (stage.getNumber() == number) return stage;
        }
        throw new IllegalArgumentException("Недопустимый номер этапа слушания: " + number);
    }
}
