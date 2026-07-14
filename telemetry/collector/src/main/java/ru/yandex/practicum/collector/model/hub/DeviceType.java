package ru.yandex.practicum.collector.model.hub;

import lombok.Getter;

@Getter
public enum DeviceType {

    MOTION_SENSOR(1),
    LIGHT_SENSOR(2),
    TEMPERATURE_SENSOR(3),
    CLIMATE_SENSOR(4),
    SWITCH_SENSOR(5),
    DEVICE_TYPE_NOT_SET(0);

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
        return DEVICE_TYPE_NOT_SET;
    }
}
