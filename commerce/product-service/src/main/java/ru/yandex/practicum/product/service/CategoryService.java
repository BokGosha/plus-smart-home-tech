package ru.yandex.practicum.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.product.dto.CategoryDto;
import ru.yandex.practicum.product.dto.CreateCategoryRequest;
import ru.yandex.practicum.product.entity.Category;
import ru.yandex.practicum.product.exception.NotFoundException;
import ru.yandex.practicum.product.mapper.CategoryMapper;
import ru.yandex.practicum.product.repository.CategoryRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryDto> getCategories() {
        return categoryMapper.toCategoryDtoList(categoryRepository.findAll());
    }

    public CategoryDto getCategoryById(Long id) {
        Category category = findCategoryById(id);

        return categoryMapper.toCategoryDto(category);
    }

    @Transactional
    public CategoryDto createCategory(CreateCategoryRequest createCategoryRequest) {
        String name = createCategoryRequest.name();
        if (categoryRepository.findByName(name).isPresent()) {
            throw new IllegalArgumentException("Категория с именем=" + name + " уже существует");
        }

        Category category = categoryMapper.toCategory(createCategoryRequest);

        categoryRepository.save(category);

        return categoryMapper.toCategoryDto(category);
    }

    public Category findCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Категория с id=" + id + " не найдена"));
    }
}
