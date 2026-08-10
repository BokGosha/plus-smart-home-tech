package ru.yandex.practicum.analyzer.controller;

import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.analyzer.model.Action;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hub_router.HubRouterControllerGrpc;

import java.time.Instant;

@Service
@Slf4j
public class AnalyzerController {

    private final HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterControllerBlockingStub;

    public AnalyzerController(@GrpcClient("hub-router")
                             HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterControllerBlockingStub) {
        this.hubRouterControllerBlockingStub = hubRouterControllerBlockingStub;
    }

    public void sendAction(String hubId, String scenarioName, String sensorId, Action action, Instant timestamp) {
        DeviceActionProto.Builder actionBuilder = DeviceActionProto.newBuilder()
                .setSensorId(sensorId)
                .setType(ActionTypeProto.valueOf(action.getType()));

        if (action.getValue() != null) {
            actionBuilder.setValue(action.getValue());
        }

        DeviceActionRequest request = DeviceActionRequest.newBuilder()
                .setHubId(hubId)
                .setScenarioName(scenarioName)
                .setAction(actionBuilder.build())
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(timestamp.getEpochSecond())
                        .setNanos(timestamp.getNano())
                        .build())
                .build();

        log.info("Отправляем действие '{}' по сценарию '{}' на датчик '{}' хаба '{}'",
                action.getType(), scenarioName, sensorId, hubId);
        hubRouterControllerBlockingStub.handleDeviceAction(request);
    }
}
