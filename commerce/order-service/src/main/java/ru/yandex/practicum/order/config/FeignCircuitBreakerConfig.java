package ru.yandex.practicum.order.config;

import org.springframework.cloud.openfeign.CircuitBreakerNameResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignCircuitBreakerConfig {

    /**
     * По умолчанию Spring Cloud OpenFeign называет CircuitBreaker по сигнатуре метода
     * (например, {@code ProductClient#getProductById(Long)}), из-за чего секции
     * resilience4j.circuitbreaker.instances.{product-service,inventory-service}
     * не применяются. Возвращаем имя Feign-клиента, чтобы конфигурация контуров
     * (в т.ч. ignoreExceptions) реально привязывалась.
     */
    @Bean
    public CircuitBreakerNameResolver circuitBreakerNameResolver() {
        return (feignClientName, target, method) -> feignClientName;
    }
}
