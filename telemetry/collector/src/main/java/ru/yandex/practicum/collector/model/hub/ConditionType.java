package ru.yandex.practicum.collector.model.hub;

import lombok.Getter;

@Getter
public enum ConditionType {

    MOTION(0),
    LUMINOSITY(1),
    SWITCH(2),
    TEMPERATURE(3),
    CO2LEVEL(4),
    HUMIDITY(5);

    private final int value;

    ConditionType(int value) {
        this.value = value;
    }

    public static ConditionType fromValue(int value) {
        for (ConditionType type : ConditionType.values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown condition type: " + value);
    }
}
