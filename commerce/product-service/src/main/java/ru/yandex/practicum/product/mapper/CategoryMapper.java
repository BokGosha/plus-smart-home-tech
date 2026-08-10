package ru.yandex.practicum.product.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.product.dto.CategoryDto;
import ru.yandex.practicum.product.dto.CreateCategoryRequest;
import ru.yandex.practicum.product.entity.Category;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toCategory(CreateCategoryRequest createCategoryRequest);

    CategoryDto toCategoryDto(Category category);

    List<CategoryDto> toCategoryDtoList(List<Category> categories);
}
