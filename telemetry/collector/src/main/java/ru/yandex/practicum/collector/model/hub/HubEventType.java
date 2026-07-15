package ru.yandex.practicum.collector.model.hub;

import lombok.Getter;

@Getter
public enum HubEventType {

    DEVICE_ADDED(0),
    DEVICE_REMOVED(1),
    SCENARIO_ADDED(2),
    SCENARIO_REMOVED(3);

    private final int value;

    HubEventType(int value) {
        this.value = value;
    }

    public static HubEventType fromValue(int value) {
        for (HubEventType type : HubEventType.values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown hub event type: " + value);
    }
}
