package ru.yandex.practicum.product.mapper;

import org.mapstruct.*;
import ru.yandex.practicum.product.dto.CreateProductRequest;
import ru.yandex.practicum.product.dto.ProductDto;
import ru.yandex.practicum.product.dto.UpdateProductRequest;
import ru.yandex.practicum.product.entity.Product;

import java.util.List;

@Mapper(componentModel = "spring", uses = CategoryMapper.class)
public interface ProductMapper {

    Product toProduct(CreateProductRequest createProductRequest);

    ProductDto toProductDto(Product product);

    List<ProductDto> toProductDtoList(List<Product> products);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProduct(UpdateProductRequest updateProductRequest, @MappingTarget Product product);
}
