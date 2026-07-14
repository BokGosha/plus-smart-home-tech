package ru.yandex.practicum.collector.model.hub;

import lombok.Getter;

@Getter
public enum ConditionOperation {

    EQUALS(1),
    GREATER_THAN(2),
    LOWER_THAN(3),
    CONDITION_OPERATION_NOT_SET(0);

    private final int value;

    ConditionOperation(int value) {
        this.value = value;
    }

    public static ConditionOperation fromValue(int value) {
        for (ConditionOperation operation : ConditionOperation.values()) {
            if (operation.value == value) {
                return operation;
            }
        }
        return CONDITION_OPERATION_NOT_SET;
    }
}
