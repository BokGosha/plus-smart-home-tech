package ru.yandex.practicum.collector.model.hub;

import lombok.Getter;

@Getter
public enum ActionType {

    ACTIVATE(1),
    DEACTIVATE(2),
    INVERSE(3),
    SET_VALUE(4),
    ACTION_TYPE_NOT_SET(0);

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
        return ACTION_TYPE_NOT_SET;
    }
}
