package ru.yandex.practicum.collector.model.hub;

import lombok.Getter;

@Getter
public enum ActionType {

    ACTIVATE(0),
    DEACTIVATE(1),
    INVERSE(2),
    SET_VALUE(3);

    private final int value;

    ActionType(int value) {
        this.value = value;
    }

    public static ActionType fromValue(int value) {
        for (ActionType type : ActionType.values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown action type: " + value);
    }
}
