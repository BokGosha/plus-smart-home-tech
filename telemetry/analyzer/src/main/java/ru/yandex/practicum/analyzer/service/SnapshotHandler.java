package ru.yandex.practicum.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.analyzer.controller.AnalyzerController;
import ru.yandex.practicum.analyzer.model.Condition;
import ru.yandex.practicum.analyzer.model.Scenario;
import ru.yandex.practicum.analyzer.model.Sensor;
import ru.yandex.practicum.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class SnapshotHandler {

    private final ScenarioRepository scenarioRepository;
    private final AnalyzerController analyzerController;

    @Transactional(readOnly = true)
    public void handle(SensorsSnapshotAvro snapshot) {
        String hubId = snapshot.getHubId();

        scenarioRepository.findByHubId(hubId).stream()
                .filter(scenario -> isTriggered(scenario, snapshot))
                .forEach(scenario -> execute(scenario, snapshot));
    }

    private boolean isTriggered(Scenario scenario, SensorsSnapshotAvro snapshot) {
        Map<String, SensorStateAvro> states = snapshot.getSensorsState();

        return !scenario.getConditions().isEmpty()
                && scenario.getConditions().entrySet().stream()
                .allMatch(entry -> checkCondition(entry.getKey(), entry.getValue(), states));
    }

    private boolean checkCondition(Sensor sensor, Condition condition, Map<String, SensorStateAvro> states) {
        SensorStateAvro state = states.get(sensor.getId());
        if (state == null) {
            return false;
        }

        Integer current = extractValue(condition.getType(), state.getData());
        if (current == null || condition.getValue() == null) {
            return false;
        }

        return compare(condition.getOperation(), current, condition.getValue());
    }

    private Integer extractValue(String conditionType, Object data) {
        return switch (conditionType) {
            case "TEMPERATURE" -> {
                if (data instanceof TemperatureSensorAvro temperature) {
                    yield temperature.getTemperatureC();
                } else if (data instanceof ClimateSensorAvro climate) {
                    yield climate.getTemperatureC();
                }
                yield null;
            }
            case "HUMIDITY" -> data instanceof ClimateSensorAvro climate ? climate.getHumidity() : null;
            case "CO2LEVEL" -> data instanceof ClimateSensorAvro climate ? climate.getCo2Level() : null;
            case "LUMINOSITY" -> data instanceof LightSensorAvro light ? light.getLuminosity() : null;
            case "MOTION" -> data instanceof MotionSensorAvro motion ? (motion.getMotion() ? 1 : 0) : null;
            case "SWITCH" -> data instanceof SwitchSensorAvro switchSensor ? (switchSensor.getState() ? 1 : 0) : null;
            default -> {
                log.warn("Неизвестный тип условия: {}", conditionType);
                yield null;
            }
        };
    }

    private boolean compare(String operation, int current, int reference) {
        return switch (operation) {
            case "EQUALS" -> current == reference;
            case "GREATER_THAN" -> current > reference;
            case "LOWER_THAN" -> current < reference;
            default -> {
                log.warn("Неизвестная операция условия: {}", operation);
                yield false;
            }
        };
    }

    private void execute(Scenario scenario, SensorsSnapshotAvro snapshot) {
        log.info("Сработал сценарий '{}' хаба '{}'", scenario.getName(), scenario.getHubId());
        scenario.getActions().forEach((sensor, action) ->
                analyzerController.sendAction(
                        scenario.getHubId(),
                        scenario.getName(),
                        sensor.getId(),
                        action,
                        snapshot.getTimestamp()));
    }
}
