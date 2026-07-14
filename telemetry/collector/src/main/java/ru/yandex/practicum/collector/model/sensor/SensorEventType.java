package ru.yandex.practicum.collector.model.sensor;

import lombok.Getter;

@Getter
public enum SensorEventType {

    MOTION_SENSOR_EVENT(1),
    TEMPERATURE_SENSOR_EVENT(2),
    LIGHT_SENSOR_EVENT(3),
    CLIMATE_SENSOR_EVENT(4),
    SWITCH_SENSOR_EVENT(5),
    SENSOR_EVENT_TYPE_NOT_SET(0);

    private final int value;

    SensorEventType(int value) {
        this.value = value;
    }

    public static SensorEventType fromValue(int value) {
        for (SensorEventType type : SensorEventType.values()) {
            if (type.value == value) {
                return type;
            }
        }
        return SENSOR_EVENT_TYPE_NOT_SET;
    }
}
