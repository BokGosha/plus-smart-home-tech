package ru.yandex.practicum.collector.service.sensor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.model.sensor.LightSensorEvent;
import ru.yandex.practicum.grpc.telemetry.event.LightSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class LightSensorEventHandler implements SensorEventHandler {

    private final SensorEventService sensorEventService;

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR;
    }

    @Override
    public void handle(SensorEventProto event) {
        LightSensorEvent dto = new LightSensorEvent();
        dto.setId(event.getId());
        dto.setHubId(event.getHubId());
        if (event.hasTimestamp()) {
            dto.setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()));
        }

        LightSensorProto payload = event.getLightSensor();
        dto.setLinkQuality(payload.getLinkQuality());
        dto.setLuminosity(payload.getLuminosity());

        sensorEventService.collect(dto);
    }
}
