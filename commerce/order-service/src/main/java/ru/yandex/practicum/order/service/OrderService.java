package ru.yandex.practicum.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.order.dto.CreateOrderRequest;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.order.dto.OrderItemRequest;
import ru.yandex.practicum.order.entity.Order;
import ru.yandex.practicum.order.entity.OrderItem;
import ru.yandex.practicum.order.exception.NotFoundException;
import ru.yandex.practicum.order.mapper.OrderItemMapper;
import ru.yandex.practicum.order.mapper.OrderMapper;
import ru.yandex.practicum.order.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    @Transactional
    public OrderDto createOrder(CreateOrderRequest createOrderRequest) {
        Order order = orderMapper.toOrder(createOrderRequest);

        for (OrderItemRequest itemRequest : createOrderRequest.items()) {
            OrderItem item = orderItemMapper.toOrderItem(itemRequest);

            order.addItem(item);
        }

        order.setStatus("CREATED");
        order.setCreatedAt(java.time.LocalDateTime.now());
        order.setTotalPrice(calculateTotalPrice(order.getItems()));

        orderRepository.save(order);

        return orderMapper.toOrderDto(order);
    }

    private BigDecimal calculateTotalPrice(List<OrderItem> items) {
        return items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public OrderDto getOrderById(Long id) {
        Order order = findOrderById(id);

        return orderMapper.toOrderDto(order);
    }

    public List<OrderDto> getOrders() {
        return orderMapper.toOrderDtoList(orderRepository.findAllWithItems());
    }

    public List<OrderDto> getOrdersByEmail(String email) {
        return orderMapper.toOrderDtoList(orderRepository.findByEmailWithItems(email));
    }

    private Order findOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Заказ с id=" + id + " не найден"));
    }
}
