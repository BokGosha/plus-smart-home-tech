package ru.yandex.practicum.collector.model.hub;

import lombok.Getter;

@Getter
public enum ConditionType {

    MOTION(1),
    LUMINOSITY(2),
    SWITCH(3),
    TEMPERATURE(4),
    CO2LEVEL(5),
    HUMIDITY(6),
    CONDITION_TYPE_NOT_SET(0);

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
        return CONDITION_TYPE_NOT_SET;
    }
}
