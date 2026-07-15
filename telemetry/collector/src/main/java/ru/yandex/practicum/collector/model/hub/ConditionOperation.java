package ru.yandex.practicum.collector.model.hub;

import lombok.Getter;

@Getter
public enum ConditionOperation {

    EQUALS(0),
    GREATER_THAN(1),
    LOWER_THAN(2);

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
        throw new IllegalArgumentException("Unknown condition operation: " + value);
    }
}
