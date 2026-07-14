package ru.yandex.practicum.collector.mapping;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.model.hub.*;
import ru.yandex.practicum.collector.model.hub.HubEvent;
import ru.yandex.practicum.grpc.telemetry.event.*;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class HubEventMapper {

    public HubEventProto mapHubEvent(HubEvent hubEvent) {
        HubEventProto.Builder builder = HubEventProto.newBuilder()
                .setHubId(hubEvent.getHubId())
                .setTimestamp(com.google.protobuf.Timestamp.newBuilder()
                        .setSeconds(hubEvent.getTimestamp().getEpochSecond())
                        .setNanos(hubEvent.getTimestamp().getNano())
                        .build());

        mapPayload(hubEvent, builder);
        return builder.build();
    }

    private void mapPayload(HubEvent hubEvent, HubEventProto.Builder builder) {
        if (hubEvent instanceof DeviceAddedEvent deviceAdded) {
            builder.setDeviceAdded(DeviceAddedEventProto.newBuilder()
                    .setId(deviceAdded.getId())
                    .setDeviceType(DeviceTypeProto.forNumber(deviceAdded.getDeviceType().getValue()))
                    .build());
        } else if (hubEvent instanceof DeviceRemovedEvent deviceRemoved) {
            builder.setDeviceRemoved(DeviceRemovedEventProto.newBuilder()
                    .setId(deviceRemoved.getId())
                    .build());
        } else if (hubEvent instanceof ScenarioAddedEvent scenarioAdded) {
            List<DeviceActionProto> actions = scenarioAdded.getActions().stream()
                    .map(this::mapDeviceAction)
                    .collect(Collectors.toList());

            List<ScenarioConditionProto> conditions = scenarioAdded.getConditions().stream()
                    .map(this::mapScenarioCondition)
                    .collect(Collectors.toList());

            builder.setScenarioAdded(ScenarioAddedEventProto.newBuilder()
                    .setName(scenarioAdded.getName())
                    .addAllAction(actions)
                    .addAllCondition(conditions)
                    .build());
        } else if (hubEvent instanceof ScenarioRemovedEvent scenarioRemoved) {
            builder.setScenarioRemoved(ScenarioRemovedEventProto.newBuilder()
                    .setName(scenarioRemoved.getName())
                    .build());
        }
    }

    private DeviceActionProto mapDeviceAction(DeviceAction deviceAction) {
        return DeviceActionProto.newBuilder()
                .setSensorId(deviceAction.getSensorId())
                .setType(ActionTypeProto.forNumber(deviceAction.getType().getValue()))
                .setValue(deviceAction.getValue())
                .build();
    }

    private ScenarioConditionProto mapScenarioCondition(ScenarioCondition scenarioCondition) {
        ScenarioConditionProto.Builder builder = ScenarioConditionProto.newBuilder()
                .setSensorId(scenarioCondition.getSensorId())
                .setType(ConditionTypeProto.forNumber(scenarioCondition.getType().getValue()))
                .setOperation(ConditionOperationProto.forNumber(scenarioCondition.getOperation().getValue()));

        if (scenarioCondition.getBoolValue() != null) {
            builder.setBoolValue(scenarioCondition.getBoolValue());
        } else if (scenarioCondition.getIntValue() != null) {
            builder.setIntValue(scenarioCondition.getIntValue());
        }

        return builder.build();
    }
}
