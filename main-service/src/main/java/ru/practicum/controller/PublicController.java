package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.CategoryDto;
import ru.practicum.service.CategoryService;

import java.util.List;

import static ru.practicum.util.Utils.getPageable;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PublicController {
    private final CategoryService categoryService;

    @GetMapping("/categories")
    public List<CategoryDto> findAllCategories(@RequestParam(defaultValue = "0") Integer from,
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
}
