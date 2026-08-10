package ru.yandex.practicum.collector.model.hub;

import lombok.Getter;

@Getter
public enum DeviceType {

    MOTION_SENSOR(0),
    TEMPERATURE_SENSOR(1),
    LIGHT_SENSOR(2),
    CLIMATE_SENSOR(3),
    SWITCH_SENSOR(4);

    private final int value;

    DeviceType(int value) {
        this.value = value;
    }

    public static DeviceType fromValue(int value) {
        for (DeviceType type : DeviceType.values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown device type: " + value);
    }
}
