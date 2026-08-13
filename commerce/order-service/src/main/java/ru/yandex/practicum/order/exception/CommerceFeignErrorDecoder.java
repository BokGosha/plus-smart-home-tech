package ru.yandex.practicum.order.exception;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;

@Component
public class CommerceFeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        boolean isProduct = methodKey.startsWith("ProductClient");

        return switch (response.status()) {
            case 404 -> new RemoteNotFoundException(isProduct
                    ? "Товар не найден"
                    : "Складская запись не найдена");
            case 409 -> new InsufficientStockException("Недостаточно товара на складе");
            default -> new RemoteServiceException("Ошибка вызова " + methodKey);
        };
    }
}
