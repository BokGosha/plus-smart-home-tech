package ru.yandex.practicum.order.client.inventory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.order.exception.InventoryServiceUnavailableException;
import ru.yandex.practicum.order.dto.ReserveRequest;
import ru.yandex.practicum.order.dto.ReserveResponse;
import ru.yandex.practicum.order.exception.InsufficientStockException;
import ru.yandex.practicum.order.exception.RemoteNotFoundException;

@Component
public class InventoryClientFallbackFactory implements FallbackFactory<InventoryClient> {

    private static final Logger logger = LoggerFactory.getLogger(InventoryClientFallbackFactory.class.getName());

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

                logger.warn("inventory-service недоступен, операция={}, productId={}",
                        operation, request.productId(), cause);

                throw new InventoryServiceUnavailableException(request.productId(), cause);
            }
        };
    }
}
