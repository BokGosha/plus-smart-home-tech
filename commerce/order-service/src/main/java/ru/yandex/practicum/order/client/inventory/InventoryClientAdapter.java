package ru.yandex.practicum.order.client.inventory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.order.client.RemoteCallResult;
import ru.yandex.practicum.order.dto.ReserveRequest;
import ru.yandex.practicum.order.dto.ReserveResponse;
import ru.yandex.practicum.order.exception.InsufficientStockException;
import ru.yandex.practicum.order.exception.InventoryServiceUnavailableException;
import ru.yandex.practicum.order.exception.RemoteNotFoundException;
import ru.yandex.practicum.order.exception.RemoteServiceException;

import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class InventoryClientAdapter {

    private final InventoryClient inventoryClient;

    public RemoteCallResult<ReserveResponse> reserveStock(ReserveRequest request) {
        return call(() -> inventoryClient.reserveStock(request));
    }

    public RemoteCallResult<ReserveResponse> releaseStock(ReserveRequest request) {
        return call(() -> inventoryClient.releaseStock(request));
    }

    private RemoteCallResult<ReserveResponse> call(Supplier<ReserveResponse> operation) {
        try {
            return new RemoteCallResult.Success<>(operation.get());
        } catch (RemoteNotFoundException | InsufficientStockException e) {
            return new RemoteCallResult.BusinessFailure<>(e.getMessage());
        } catch (InventoryServiceUnavailableException | RemoteServiceException e) {
            return new RemoteCallResult.TechnicalFailure<>("Не удалось выполнить операцию со складом");
        }
    }
}
