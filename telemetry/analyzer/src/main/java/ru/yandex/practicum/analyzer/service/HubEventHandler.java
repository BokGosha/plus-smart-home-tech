package ru.yandex.practicum.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.analyzer.model.Action;
import ru.yandex.practicum.analyzer.model.Condition;
import ru.yandex.practicum.analyzer.model.Scenario;
import ru.yandex.practicum.analyzer.model.Sensor;
import ru.yandex.practicum.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.analyzer.repository.SensorRepository;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class HubEventHandler {

    private final SensorRepository sensorRepository;
    private final ScenarioRepository scenarioRepository;

    @Transactional
    public void handle(HubEventAvro event) {
        Object payload = event.getPayload();
        String hubId = event.getHubId();

        switch (payload) {
            case DeviceAddedEventAvro deviceAdded -> handleDeviceAdded(hubId, deviceAdded);
            case DeviceRemovedEventAvro deviceRemoved -> handleDeviceRemoved(hubId, deviceRemoved);
            case ScenarioAddedEventAvro scenarioAdded -> handleScenarioAdded(hubId, scenarioAdded);
            case ScenarioRemovedEventAvro scenarioRemoved -> handleScenarioRemoved(hubId, scenarioRemoved);
            case null, default -> log.warn("Неизвестный тип события хаба: {}", payload);
        }
    }

    private void handleDeviceAdded(String hubId, DeviceAddedEventAvro event) {
        if (sensorRepository.existsById(event.getId())) {
            log.debug("Датчик '{}' уже зарегистрирован, пропускаем", event.getId());
            return;
        }

        Sensor sensor = new Sensor();
        sensor.setId(event.getId());
        sensor.setHubId(hubId);
        sensorRepository.save(sensor);
        log.info("Добавлен датчик '{}' в хаб '{}'", sensor.getId(), hubId);
    }

    private void handleDeviceRemoved(String hubId, DeviceRemovedEventAvro event) {
        sensorRepository.findByIdAndHubId(event.getId(), hubId)
                .ifPresent(sensor -> {
                    sensorRepository.delete(sensor);
                    log.info("Удалён датчик '{}' из хаба '{}'", sensor.getId(), hubId);
                });
    }

    private void handleScenarioAdded(String hubId, ScenarioAddedEventAvro event) {
        Scenario scenario = scenarioRepository.findByHubIdAndName(hubId, event.getName())
                .orElseGet(() -> {
                    Scenario created = new Scenario();
                    created.setHubId(hubId);
                    created.setName(event.getName());
                    return created;
                });

        Map<Sensor, Condition> conditions = new HashMap<>();
        for (ScenarioConditionAvro conditionAvro : event.getConditions()) {
            sensorRepository.findByIdAndHubId(conditionAvro.getSensorId(), hubId)
                    .ifPresent(sensor -> conditions.put(sensor, toCondition(conditionAvro)));
        }

        Map<Sensor, Action> actions = new HashMap<>();
        for (DeviceActionAvro actionAvro : event.getActions()) {
            sensorRepository.findByIdAndHubId(actionAvro.getSensorId(), hubId)
                    .ifPresent(sensor -> actions.put(sensor, toAction(actionAvro)));
        }

        scenario.getConditions().clear();
        scenario.getConditions().putAll(conditions);
        scenario.getActions().clear();
        scenario.getActions().putAll(actions);

        scenarioRepository.save(scenario);
        log.info("Сохранён сценарий '{}' хаба '{}' ({} условий, {} действий)",
                scenario.getName(), hubId, conditions.size(), actions.size());
    }

    private void handleScenarioRemoved(String hubId, ScenarioRemovedEventAvro event) {
        scenarioRepository.findByHubIdAndName(hubId, event.getName())
                .ifPresent(scenario -> {
                    scenarioRepository.delete(scenario);
                    log.info("Удалён сценарий '{}' хаба '{}'", scenario.getName(), hubId);
                });
    }

    private Condition toCondition(ScenarioConditionAvro conditionAvro) {
        Condition condition = new Condition();
        condition.setType(conditionAvro.getType().name());
        condition.setOperation(conditionAvro.getOperation().name());
        condition.setValue(toInteger(conditionAvro.getValue()));
        return condition;
    }

    private Action toAction(DeviceActionAvro actionAvro) {
        Action action = new Action();
        action.setType(actionAvro.getType().name());
        action.setValue(actionAvro.getValue());
        return action;
    }

    private Integer toInteger(Object value) {
        return switch (value) {
            case null -> null;
            case Boolean bool -> bool ? 1 : 0;
            case Number number -> number.intValue();
            default -> null;
        };
    }
}
