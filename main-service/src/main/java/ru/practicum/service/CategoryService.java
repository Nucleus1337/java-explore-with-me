package ru.practicum.service;

import static ru.practicum.mapper.CategoryMapper.toDto;
import static ru.practicum.mapper.CategoryMapper.toModel;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.CategoryDto;
import ru.practicum.exception.CustomException;
import ru.practicum.model.Category;
import ru.practicum.repository.CategoryRepository;

@Service
@RequiredArgsConstructor
public class CategoryService {
  private final CategoryRepository categoryRepository;

  private Category getCategory(Long catId) {
    return categoryRepository
        .findById(catId)
        .orElseThrow(
            () ->
                new CustomException.CategoryNotFoundException(
                    String.format("Category with id=%s was not found", catId)));
  }

  @Transactional
  public CategoryDto create(CategoryDto categoryDto) {
    Category category = toModel(categoryDto);

    return toDto(categoryRepository.saveAndFlush(category));
  }

  @Transactional
  public ResponseEntity<Object> delete(Long catId) {
    // TODO: надо проверять, что с категорией не связано ни одно событие
    Category category = getCategory(catId);

    categoryRepository.delete(category);

    return ResponseEntity.ok().build();
  }

  @Transactional
  public CategoryDto update(Long catId, CategoryDto categoryDto) {
    Category category = getCategory(catId);
    category.setName(categoryDto.getName());

    return toDto(categoryRepository.saveAndFlush(category));
  }
}
