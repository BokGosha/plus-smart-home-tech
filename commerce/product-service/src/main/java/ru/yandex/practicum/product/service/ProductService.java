package ru.yandex.practicum.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.product.dto.CreateProductRequest;
import ru.yandex.practicum.product.dto.ProductDto;
import ru.yandex.practicum.product.dto.UpdateProductRequest;
import ru.yandex.practicum.product.entity.Category;
import ru.yandex.practicum.product.entity.Product;
import ru.yandex.practicum.product.exception.NotFoundException;
import ru.yandex.practicum.product.mapper.ProductMapper;
import ru.yandex.practicum.product.repository.ProductRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    private final CategoryService categoryService;

    public List<ProductDto> getProducts() {
        return productMapper.toProductDtoList(productRepository.findAllWithCategory());
    }

    public ProductDto getProductById(Long id) {
        Product product = findProductById(id);

        return productMapper.toProductDto(product);
    }

    public List<ProductDto> getProductsByCategoryId(Long categoryId) {
        categoryService.findCategoryById(categoryId);

        return productMapper.toProductDtoList(productRepository.findByCategoryIdWithCategory(categoryId));
    }

    public List<ProductDto> getProductsByName(String query) {
        return productMapper.toProductDtoList(productRepository.findByNameWithCategory(query));
    }

    @Transactional
    public ProductDto createProduct(CreateProductRequest createProductRequest) {
        String name = createProductRequest.name();
        if (productRepository.findByName(name).isPresent()) {
                throw new IllegalArgumentException("Продукт с именем=" + name + " уже существует");
        }

        Product product = productMapper.toProduct(createProductRequest);
        if (createProductRequest.categoryId() != null) {
            Category category = categoryService.findCategoryById(createProductRequest.categoryId());
            product.setCategory(category);
        }

        productRepository.save(product);

        return productMapper.toProductDto(product);
    }

    @Transactional
    public ProductDto updateProduct(Long id, UpdateProductRequest updateProductRequest) {
        Product product = findProductById(id);

        productMapper.updateProduct(updateProductRequest, product);

        if (updateProductRequest.categoryId() != null) {
            Category category = categoryService.findCategoryById(updateProductRequest.categoryId());
            product.setCategory(category);
        }

        return productMapper.toProductDto(product);
    }

    public Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Продукт с id=" + id + " не найден"));
    }
}
