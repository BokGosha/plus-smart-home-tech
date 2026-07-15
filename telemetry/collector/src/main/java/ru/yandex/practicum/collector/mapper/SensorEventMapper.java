package ru.yandex.practicum.collector.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.model.sensor.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

@Component
public class SensorEventMapper {

    public SensorEventAvro mapSensorEvent(SensorEvent sensorEvent) {
        return SensorEventAvro.newBuilder()
                .setId(sensorEvent.getId())
                .setHubId(sensorEvent.getHubId())
                .setTimestamp(sensorEvent.getTimestamp())
                .setPayload(mapPayload(sensorEvent))
                .build();
    }

    private Object mapPayload(SensorEvent sensorEvent) {
        return switch (sensorEvent.getType()) {
            case LIGHT_SENSOR_EVENT -> mapLightSensorEvent((LightSensorEvent) sensorEvent);
            case MOTION_SENSOR_EVENT -> mapMotionSensorEvent((MotionSensorEvent) sensorEvent);
            case TEMPERATURE_SENSOR_EVENT -> mapTemperatureSensorEvent((TemperatureSensorEvent) sensorEvent);
            case CLIMATE_SENSOR_EVENT -> mapClimateSensorEvent((ClimateSensorEvent) sensorEvent);
            case SWITCH_SENSOR_EVENT -> mapSwitchSensorEvent((SwitchSensorEvent) sensorEvent);
        };
    }

    private LightSensorAvro mapLightSensorEvent(LightSensorEvent sensorEvent) {
        return LightSensorAvro.newBuilder()
                .setLinkQuality(sensorEvent.getLinkQuality())
                .setLuminosity(sensorEvent.getLuminosity())
                .build();
    }

    private MotionSensorAvro mapMotionSensorEvent(MotionSensorEvent sensorEvent) {
        return MotionSensorAvro.newBuilder()
                .setLinkQuality(sensorEvent.getLinkQuality())
                .setMotion(sensorEvent.getMotion())
                .setVoltage(sensorEvent.getVoltage())
                .build();
    }

    private TemperatureSensorAvro mapTemperatureSensorEvent(TemperatureSensorEvent sensorEvent) {
        return TemperatureSensorAvro.newBuilder()
                .setTemperatureC(sensorEvent.getTemperatureC())
                .setTemperatureF(sensorEvent.getTemperatureF())
                .build();
    }

    private ClimateSensorAvro mapClimateSensorEvent(ClimateSensorEvent sensorEvent) {
        return ClimateSensorAvro.newBuilder()
                .setTemperatureC(sensorEvent.getTemperatureC())
                .setCo2Level(sensorEvent.getCo2Level())
                .setHumidity(sensorEvent.getHumidity())
                .build();
    }

    private SwitchSensorAvro mapSwitchSensorEvent(SwitchSensorEvent sensorEvent) {
        return SwitchSensorAvro.newBuilder()
                .setState(sensorEvent.getState())
                .build();
    }
}
