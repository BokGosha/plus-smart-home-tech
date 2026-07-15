package ru.yandex.practicum.collector.model.sensor;

import lombok.Getter;

@Getter
public enum SensorEventType {

    MOTION_SENSOR_EVENT(0),
    TEMPERATURE_SENSOR_EVENT(1),
    LIGHT_SENSOR_EVENT(2),
    CLIMATE_SENSOR_EVENT(3),
    SWITCH_SENSOR_EVENT(4);

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
        throw new IllegalArgumentException("Unknown sensor event type: " + value);
    }
}
