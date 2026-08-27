package ru.yandex.practicum.order.client.inventory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.order.exception.InventoryServiceUnavailableException;
import ru.yandex.practicum.order.dto.ReserveRequest;
import ru.yandex.practicum.order.dto.ReserveResponse;
import ru.yandex.practicum.order.exception.InsufficientStockException;
import ru.yandex.practicum.order.exception.RemoteNotFoundException;

@Component
@Slf4j
public class InventoryClientFallbackFactory implements FallbackFactory<InventoryClient> {

    @Override
    public InventoryClient create(Throwable cause) {
        return new InventoryClient() {

            @Override
            public ReserveResponse reserveStock(ReserveRequest request) {
                return handle(request, "резервирование");
            }

            @Override
            public ReserveResponse releaseStock(ReserveRequest request) {
                return handle(request, "снятие резерва");
            }

            private ReserveResponse handle(ReserveRequest request, String operation) {
                if (cause instanceof RemoteNotFoundException e) {
                    throw e;
                }
                if (cause instanceof InsufficientStockException e) {
                    throw e;
                }

                log.warn("inventory-service недоступен, операция={}, productId={}",
                        operation, request.productId(), cause);

                throw new InventoryServiceUnavailableException(request.productId(), cause);
            }
        };
    }
}
