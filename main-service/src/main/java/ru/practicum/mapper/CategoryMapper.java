package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.CategoryDto;
import ru.practicum.model.Category;

@UtilityClass
public class CategoryMapper {
  public static Category toModel(CategoryDto categoryDto) {
    return Category.builder().name(categoryDto.getName()).build();
  }

  public static CategoryDto toDto(Category category) {
    return CategoryDto.builder().id(category.getId()).name(category.getName()).build();
  }
}
