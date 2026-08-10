package ru.yandex.practicum.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.order.client.InventoryClient;
import ru.yandex.practicum.order.client.ProductClient;
import ru.yandex.practicum.order.dto.*;
import ru.yandex.practicum.order.entity.OrderData;
import ru.yandex.practicum.order.entity.OrderItemData;
import ru.yandex.practicum.order.exception.OrderProcessingException;

import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderOrchestrationService {

    private final OrderService orderService;
    private final ProductClient productClient;
    private final InventoryClient inventoryClient;

    public OrderDto createOrder(CreateOrderRequest createOrderRequest) {
        Map<Long, ProductDto> products = new HashMap<>();
        for (OrderItemRequest itemRequest : createOrderRequest.items()) {
            products.computeIfAbsent(itemRequest.productId(), productClient::getProductById);
        }

        for (ProductDto product : products.values()) {
            if (!product.active()) {
                throw new OrderProcessingException("Товар снят с продажи: " + product.name());
            }
        }

        Map<Long, Integer> productQuantities = new LinkedHashMap<>();
        for (OrderItemRequest itemRequest : createOrderRequest.items()) {
            productQuantities.merge(itemRequest.productId(), itemRequest.quantity(), Integer::sum);
        }

        List<ReserveRequest> reserved = new ArrayList<>();
        try {
            for (Map.Entry<Long, Integer> entry : productQuantities.entrySet()) {
                ReserveRequest reserveRequest = new ReserveRequest(
                        entry.getKey(),
                        entry.getValue()
                );
                inventoryClient.reserveStock(reserveRequest);
                reserved.add(reserveRequest);
            }

            List<OrderItemData> items = createOrderRequest.items().stream()
                    .map(itemRequest -> {
                        ProductDto product = products.get(itemRequest.productId());
                        return new OrderItemData(
                                itemRequest.productId(),
                                product.name(),
                                product.price(),
                                itemRequest.quantity());
                    })
                    .toList();

            return orderService.saveOrder(new OrderData(createOrderRequest.customerName(), createOrderRequest.customerEmail(), items));
        } catch (Exception e) {
            for (ReserveRequest request : reserved) {
                inventoryClient.releaseStock(request);
            }

            throw e;
        }
    }
}
