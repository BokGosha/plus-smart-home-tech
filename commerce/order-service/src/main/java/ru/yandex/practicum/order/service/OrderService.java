package ru.yandex.practicum.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.order.dto.*;
import ru.yandex.practicum.order.entity.Order;
import ru.yandex.practicum.order.dto.OrderData;
import ru.yandex.practicum.order.entity.OrderItem;
import ru.yandex.practicum.order.dto.OrderItemData;
import ru.yandex.practicum.order.exception.NotFoundException;
import ru.yandex.practicum.order.mapper.OrderItemMapper;
import ru.yandex.practicum.order.mapper.OrderMapper;
import ru.yandex.practicum.order.repository.OrderRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    @Transactional
    public OrderDto saveOrder(OrderData orderData) {
        Order order = new Order();
        order.setCustomerName(orderData.customerName());
        order.setCustomerEmail(orderData.customerEmail());
        order.setStatus(orderData.status());
        order.setStatusDetails(orderData.statusDetails());
        order.setCreatedAt(LocalDateTime.now());

        for (OrderItemData itemData : orderData.items()) {
            OrderItem item = orderItemMapper.toOrderItem(itemData);
            order.addItem(item);
        }

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
