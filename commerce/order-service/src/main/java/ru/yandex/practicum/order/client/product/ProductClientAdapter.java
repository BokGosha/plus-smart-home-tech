package ru.yandex.practicum.order.client.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.order.client.RemoteCallResult;
import ru.yandex.practicum.order.dto.ProductDto;
import ru.yandex.practicum.order.exception.ProductServiceUnavailableException;
import ru.yandex.practicum.order.exception.RemoteNotFoundException;
import ru.yandex.practicum.order.exception.RemoteServiceException;

@Component
@RequiredArgsConstructor
public class ProductClientAdapter {

    private final ProductClient productClient;

    public RemoteCallResult<ProductDto> getProduct(Long productId) {
        try {
            return new RemoteCallResult.Success<>(productClient.getProductById(productId));
        } catch (RemoteNotFoundException e) {
            return new RemoteCallResult.BusinessFailure<>(e.getMessage());
        } catch (ProductServiceUnavailableException | RemoteServiceException e) {
            return new RemoteCallResult.TechnicalFailure<>("Не удалось получить данные товара");
        }
    }
}
