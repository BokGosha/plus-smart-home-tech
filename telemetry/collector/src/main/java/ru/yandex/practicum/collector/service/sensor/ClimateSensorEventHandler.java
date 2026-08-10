package ru.yandex.practicum.collector.service.sensor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.model.sensor.ClimateSensorEvent;
import ru.yandex.practicum.grpc.telemetry.event.ClimateSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class ClimateSensorEventHandler implements SensorEventHandler {

    private final SensorEventService sensorEventService;

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.CLIMATE_SENSOR;
    }

    @Override
    public void handle(SensorEventProto event) {
        ClimateSensorEvent dto = new ClimateSensorEvent();
        dto.setId(event.getId());
        dto.setHubId(event.getHubId());
        if (event.hasTimestamp()) {
            dto.setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()));
        }

        ClimateSensorProto payload = event.getClimateSensor();
        dto.setTemperatureC(payload.getTemperatureC());
        dto.setHumidity(payload.getHumidity());
        dto.setCo2Level(payload.getCo2Level());

        sensorEventService.collect(dto);
    }
}
