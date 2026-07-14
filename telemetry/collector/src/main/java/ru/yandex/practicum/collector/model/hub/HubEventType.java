package ru.yandex.practicum.collector.model.hub;

import lombok.Getter;

@Getter
public enum HubEventType {

    DEVICE_ADDED(1),
    DEVICE_REMOVED(2),
    SCENARIO_ADDED(3),
    SCENARIO_REMOVED(4),
    HUB_EVENT_TYPE_NOT_SET(0);

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
        return HUB_EVENT_TYPE_NOT_SET;
    }
}
