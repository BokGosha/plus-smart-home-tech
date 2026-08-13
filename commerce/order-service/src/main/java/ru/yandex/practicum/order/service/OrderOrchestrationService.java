package ru.yandex.practicum.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.order.client.RemoteCallResult;
import ru.yandex.practicum.order.client.inventory.InventoryClientAdapter;
import ru.yandex.practicum.order.client.product.ProductClientAdapter;
import ru.yandex.practicum.order.dto.*;
import ru.yandex.practicum.order.dto.OrderData;
import ru.yandex.practicum.order.dto.OrderItemData;
import ru.yandex.practicum.order.exception.OrderProcessingException;

import java.math.BigDecimal;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderOrchestrationService {

    private final OrderService orderService;
    private final ProductClientAdapter productClientAdapter;
    private final InventoryClientAdapter inventoryClientAdapter;

    public OrderDto createOrder(CreateOrderRequest request) {
        Map<Long, ProductDto> products = new HashMap<>();
        boolean degraded = false;

        for (OrderItemRequest itemRequest : request.items()) {
            Long productId = itemRequest.productId();
            if (products.containsKey(productId)) {
                continue;
            }
            switch (productClientAdapter.getProduct(productId)) {
                case RemoteCallResult.Success<ProductDto> s -> products.put(productId, s.data());
                case RemoteCallResult.BusinessFailure<ProductDto> f -> throw new OrderProcessingException(f.message());
                case RemoteCallResult.TechnicalFailure<ProductDto> f -> degraded = true;
            }
            if (degraded) {
                break;
            }
        }

        if (!degraded) {
            for (ProductDto product : products.values()) {
                if (!Boolean.TRUE.equals(product.active())) {
                    throw new OrderProcessingException("Товар снят с продажи: " + product.name());
                }
            }
        }

        List<ReserveRequest> reserved = new ArrayList<>();
        if (!degraded) {
            Map<Long, Integer> quantities = new LinkedHashMap<>();
            for (OrderItemRequest itemRequest : request.items()) {
                quantities.merge(itemRequest.productId(), itemRequest.quantity(), Integer::sum);
            }

            try {
                for (Map.Entry<Long, Integer> entry : quantities.entrySet()) {
                    ReserveRequest reserveRequest = new ReserveRequest(entry.getKey(), entry.getValue());
                    switch (inventoryClientAdapter.reserveStock(reserveRequest)) {
                        case RemoteCallResult.Success<ReserveResponse> s -> reserved.add(reserveRequest);
                        case RemoteCallResult.BusinessFailure<ReserveResponse> f ->
                                throw new OrderProcessingException(f.message());
                        case RemoteCallResult.TechnicalFailure<ReserveResponse> f -> degraded = true;
                    }
                    if (degraded) {
                        break;
                    }
                }
            } catch (OrderProcessingException e) {
                releaseAll(reserved);
                throw e;
            }
        }

        List<OrderItemData> items = request.items().stream()
                .map(itemRequest -> toItemData(itemRequest, products.get(itemRequest.productId())))
                .toList();

        String status = degraded ? "PENDING_CONFIRMATION" : "CONFIRMED";
        String details = degraded ? "Заказ требует ручной проверки: данные получены не полностью" : null;

        try {
            return orderService.saveOrder(new OrderData(
                    request.customerName(), request.customerEmail(), items));
        } catch (Exception e) {
            releaseAll(reserved);
            throw e;
        }
    }

    private void releaseAll(List<ReserveRequest> reserved) {
        for (ReserveRequest request : reserved) {
            if (!(inventoryClientAdapter.releaseStock(request) instanceof RemoteCallResult.Success)) {
                log.error("Не удалось снять резерв productId={}, quantity={}",
                        request.productId(), request.quantity());
            }
        }
    }

    private OrderItemData toItemData(OrderItemRequest itemRequest, ProductDto product) {
        if (product == null) {
            return new OrderItemData(
                    itemRequest.productId(),
                    "Товар #" + itemRequest.productId() + " (ожидает проверки)",
                    BigDecimal.ZERO,
                    itemRequest.quantity());
        }
        return new OrderItemData(itemRequest.productId(), product.name(),
                product.price(), itemRequest.quantity());
    }
}
