package ru.practicum.controller;

import static ru.practicum.util.Utils.getPageable;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.service.CategoryService;
import ru.practicum.service.CompilationService;
import ru.practicum.service.EventService;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PublicController {
  private final CategoryService categoryService;
  private final CompilationService compilationService;
  private final EventService eventService;

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

  @GetMapping("/events/{id}")
  public EventFullDto findEventById(@PathVariable Long id, HttpServletRequest request) {
    log.info("GET /events/{id}: id={}", id);

    return eventService.findEventById(
        id, request.getServerName(), request.getRequestURI(), request.getRemoteAddr());
  }

  @GetMapping("/events")
  public List<EventShortDto> findEvents(
      @RequestParam(required = false) String text,
      @RequestParam(required = false) Long[] categories,
      @RequestParam(required = false) Boolean paid,
      @RequestParam(required = false) String rangeStart,
      @RequestParam(required = false) String rangeEnd,
      @RequestParam(defaultValue = "false") Boolean onlyAvailable,
      @RequestParam(required = false) String sort,
      @RequestParam(defaultValue = "0") Integer from,
      @RequestParam(defaultValue = "10") Integer size,
      HttpServletRequest request) {
    log.info(
        "GET /events: text={}, categories={}, paid={}, rangeStart={}, rangeEnd={}, "
            + "onlyAvailable={}, sort={}, from={}, size={}",
        text,
        categories,
        paid,
        rangeStart,
        rangeEnd,
        onlyAvailable,
        sort,
        from,
        size);
    Pageable pageable = getPageable(from, size);

    return eventService.findAllEventsWithFilters(
        text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, request, pageable);
  }
}
