package ru.yandex.practicum.collector.mapping;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.model.hub.*;
import ru.yandex.practicum.collector.model.sensor.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.List;

@Component
public class HubEventMapper {

    public HubEventAvro mapHubEvent(HubEvent hubEvent) {
        return HubEventAvro.newBuilder()
                .setHubId(hubEvent.getHubId())
                .setTimestamp(hubEvent.getTimestamp())
                .setPayload(mapPayload(hubEvent))
                .build();
    }

    private Object mapPayload(HubEvent hubEvent) {
        return switch (hubEvent.getType()) {
            case DEVICE_ADDED -> mapDeviceAdded((DeviceAddedEvent) hubEvent);
            case DEVICE_REMOVED -> mapDeviceRemoved((DeviceRemovedEvent) hubEvent);
            case SCENARIO_ADDED -> mapScenarioAdded((ScenarioAddedEvent) hubEvent);
            case SCENARIO_REMOVED -> mapScenarioRemoved((ScenarioRemovedEvent) hubEvent);
        };
    }

    private DeviceAddedEventAvro mapDeviceAdded(DeviceAddedEvent hubEvent) {
        return DeviceAddedEventAvro.newBuilder()
                .setId(hubEvent.getId())
                .setType(DeviceTypeAvro.valueOf(hubEvent.getDeviceType().name()))
                .build();
    }

    private DeviceRemovedEventAvro mapDeviceRemoved(DeviceRemovedEvent hubEvent) {
        return DeviceRemovedEventAvro.newBuilder()
                .setId(hubEvent.getId())
                .build();
    }

    private ScenarioAddedEventAvro mapScenarioAdded(ScenarioAddedEvent event) {
        List<ScenarioConditionAvro> conditions = event.getConditions().stream()
                .map(this::mapScenarioCondition)
                .toList();

        List<DeviceActionAvro> actions = event.getActions().stream()
                .map(this::mapDeviceAction)
                .toList();

        return ScenarioAddedEventAvro.newBuilder()
                .setName(event.getName())
                .setConditions(conditions)
                .setActions(actions)
                .build();
    }

    private DeviceActionAvro mapDeviceAction(DeviceAction deviceAction) {
        return DeviceActionAvro.newBuilder()
                .setSensorId(deviceAction.getSensorId())
                .setType(ActionTypeAvro.valueOf(deviceAction.getType().name()))
                .setValue(deviceAction.getValue())
                .build();
    }

    private ScenarioConditionAvro mapScenarioCondition(ScenarioCondition scenarioCondition) {
        ScenarioConditionAvro.Builder builder = ScenarioConditionAvro.newBuilder()
                .setSensorId(scenarioCondition.getSensorId())
                .setType(ConditionTypeAvro.valueOf(scenarioCondition.getType().name()))
                .setOperation(ConditionOperationAvro.valueOf(scenarioCondition.getOperation().name()));

        if (scenarioCondition.getBoolValue() != null) {
            builder.setValue(scenarioCondition.getBoolValue() ? 1 : 0);
        } else if (scenarioCondition.getIntValue() != null) {
            builder.setValue(scenarioCondition.getIntValue());
        }

        return builder.build();
    }

    private ScenarioRemovedEventAvro mapScenarioRemoved(ScenarioRemovedEvent hubEvent) {
        return ScenarioRemovedEventAvro.newBuilder()
                .setName(hubEvent.getName())
                .build();
    }
}
