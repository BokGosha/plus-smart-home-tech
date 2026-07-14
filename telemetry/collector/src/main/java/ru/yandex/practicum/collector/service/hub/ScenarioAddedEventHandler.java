package ru.yandex.practicum.collector.service.hub;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.model.hub.ScenarioAddedEvent;
import ru.yandex.practicum.collector.model.hub.ScenarioCondition;
import ru.yandex.practicum.collector.model.hub.DeviceAction;
import ru.yandex.practicum.collector.model.hub.ConditionType;
import ru.yandex.practicum.collector.model.hub.ConditionOperation;
import ru.yandex.practicum.collector.model.hub.ActionType;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ScenarioAddedEventHandler implements HubEventHandler {

    private final HubEventService hubEventService;

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

    @Override
    public void handle(HubEventProto event) {
        ScenarioAddedEvent dto = new ScenarioAddedEvent();
        dto.setHubId(event.getHubId());
        if (event.hasTimestamp()) {
            dto.setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()));
        }

        ScenarioAddedEventProto payload = event.getScenarioAdded();
        dto.setName(payload.getName());

        List<ScenarioCondition> conditions = payload.getConditionList().stream()
                .map(this::toCondition)
                .collect(Collectors.toList());

        List<DeviceAction> actions = payload.getActionList().stream()
                .map(this::toAction)
                .collect(Collectors.toList());

        dto.setConditions(conditions);
        dto.setActions(actions);

        hubEventService.collect(dto);
    }

    private ScenarioCondition toCondition(ScenarioConditionProto p) {
        ScenarioCondition c = new ScenarioCondition();
        c.setSensorId(p.getSensorId());
        c.setType(ConditionType.fromValue(p.getTypeValue()));
        c.setOperation(ConditionOperation.fromValue(p.getOperationValue()));
        if (p.hasBoolValue()) {
            c.setBoolValue(p.getBoolValue());
        } else {
            c.setIntValue(p.getIntValue());
        }
        return c;
    }

    private DeviceAction toAction(DeviceActionProto p) {
        DeviceAction a = new DeviceAction();
        a.setSensorId(p.getSensorId());
        a.setType(ActionType.fromValue(p.getTypeValue()));
        if (p.hasValue()) {
            a.setValue(p.getValue());
        }
        return a;
    }
}
