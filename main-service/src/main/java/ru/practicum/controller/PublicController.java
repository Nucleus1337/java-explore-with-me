package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.CompilationDto;
import ru.practicum.service.CategoryService;
import ru.practicum.service.CompilationService;

import java.util.List;

import static ru.practicum.util.Utils.getPageable;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PublicController {
  private final CategoryService categoryService;
  private final CompilationService compilationService;

  @GetMapping("/categories")
  public List<CategoryDto> findAllCategories(
      @RequestParam(defaultValue = "0") Integer from,
      @RequestParam(defaultValue = "10") Integer size) {
    log.info("GET /categories: from={}, size={}", from, size);
    Pageable pageable = getPageable(from, size);

    return categoryService.findAllCategories(pageable);
  }

  @GetMapping("/categories/{catId}")
  public CategoryDto findCategoryById(@PathVariable Long catId) {
    log.info("GET /categories/{catId}: catId={}", catId);

    return categoryService.findCategoryById(catId);
  }

  @GetMapping("/compilations")
  public List<CompilationDto> findCompilations(
      @RequestParam Boolean pinned,
      @RequestParam(defaultValue = "0") Integer from,
      @RequestParam(defaultValue = "10") Integer size) {
    log.info("GET /compilation: pinned={}, from={}, size={}", pinned, from, size);

    Pageable pageable = getPageable(from, size);

    return compilationService.findCompilations(pinned, pageable);
  }

  @GetMapping("/compilations/{compId}")
  public CompilationDto findCompilationById(@PathVariable Long compId) {
    log.info("GET /compilations/{compId}: compId={}", compId);

    return compilationService.findCompilationById(compId);
  }
}
