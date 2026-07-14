package ru.yandex.practicum.collector.mapping;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.model.sensor.*;
import ru.yandex.practicum.collector.model.sensor.SensorEvent;
import ru.yandex.practicum.grpc.telemetry.event.*;

@Component
public class SensorEventMapper {

    public SensorEventProto mapSensorEvent(SensorEvent sensorEvent) {
        SensorEventProto.Builder builder = SensorEventProto.newBuilder()
                .setId(sensorEvent.getId())
                .setHubId(sensorEvent.getHubId())
                .setTimestamp(com.google.protobuf.Timestamp.newBuilder()
                        .setSeconds(sensorEvent.getTimestamp().getEpochSecond())
                        .setNanos(sensorEvent.getTimestamp().getNano())
                        .build());

        mapPayload(sensorEvent, builder);
        return builder.build();
    }

    private void mapPayload(SensorEvent sensorEvent, SensorEventProto.Builder builder) {
        if (sensorEvent instanceof LightSensorEvent lightEvent) {
            builder.setLightSensor(LightSensorProto.newBuilder()
                    .setLinkQuality(lightEvent.getLinkQuality())
                    .setLuminosity(lightEvent.getLuminosity())
                    .build());
        } else if (sensorEvent instanceof MotionSensorEvent motionEvent) {
            builder.setMotionSensor(MotionSensorProto.newBuilder()
                    .setLinkQuality(motionEvent.getLinkQuality())
                    .setMotion(motionEvent.getMotion())
                    .setVoltage(motionEvent.getVoltage())
                    .build());
        } else if (sensorEvent instanceof TemperatureSensorEvent tempEvent) {
            builder.setTemperatureSensor(TemperatureSensorProto.newBuilder()
                    .setTemperatureC(tempEvent.getTemperatureC())
                    .setTemperatureF(tempEvent.getTemperatureF())
                    .build());
        } else if (sensorEvent instanceof ClimateSensorEvent climateEvent) {
            builder.setClimateSensor(ClimateSensorProto.newBuilder()
                    .setTemperatureC(climateEvent.getTemperatureC())
                    .setCo2Level(climateEvent.getCo2Level())
                    .setHumidity(climateEvent.getHumidity())
                    .build());
        } else if (sensorEvent instanceof SwitchSensorEvent switchEvent) {
            builder.setSwitchSensor(SwitchSensorProto.newBuilder()
                    .setState(switchEvent.getState())
                    .build());
        }
    }
}
