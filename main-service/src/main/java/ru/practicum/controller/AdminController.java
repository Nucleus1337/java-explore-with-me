package ru.practicum.controller;

import static ru.practicum.util.Utils.getPageable;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequestDto;
import ru.practicum.dto.UpdateEventAdminRequestDto;
import ru.practicum.dto.UserDto;
import ru.practicum.service.CategoryService;
import ru.practicum.service.CompilationService;
import ru.practicum.service.EventService;
import ru.practicum.service.UserService;

@RestController
@RequestMapping("/admin")
@Slf4j
@RequiredArgsConstructor
public class AdminController {
  private final UserService userService;
  private final CategoryService categoryService;
  private final CompilationService compilationService;
  private final EventService eventService;

  @PostMapping("/users")
  @ResponseStatus(HttpStatus.CREATED)
  public UserDto createUser(@RequestBody @Valid UserDto userDto) {
    log.info("POST /admin/users: {}", userDto);

    return userService.create(userDto);
  }

  @GetMapping("/users")
  public List<UserDto> findUsers(
      @RequestParam(required = false) List<Long> ids,
      @RequestParam(required = false, defaultValue = "0") Integer from,
      @RequestParam(required = false, defaultValue = "10") Integer size) {
    log.info("GET /admin/users: ids={}, from={}, size={}", ids, from, size);
    Pageable pageable = getPageable(from, size);

    return userService.findUsers(ids, pageable);
  }

  @DeleteMapping("/users/{userId}")
  public ResponseEntity<Object> deleteUser(@PathVariable(name = "userId") Long userId) {
    log.info("DELETE /admin/users/{userId}: userId={}", userId);

    return userService.deleteUser(userId);
  }

  @PostMapping("/categories")
  @ResponseStatus(HttpStatus.CREATED)
  public CategoryDto createCategory(@RequestBody @Valid CategoryDto categoryDto) {
    log.info("POST /admin/categories: categoryDto={}", categoryDto);

    return categoryService.create(categoryDto);
  }

  @DeleteMapping("/categories/{catId}")
  public ResponseEntity<Object> deleteCategory(@PathVariable(name = "catId") Long catId) {
    log.info("DELETE /admin/categories/{catId}: catId={}", catId);

    return categoryService.delete(catId);
  }

  @PatchMapping("/categories/{catId}")
  public CategoryDto updateCategory(
      @PathVariable(name = "catId") Long catId, @RequestBody CategoryDto categoryDto) {
    log.info("PATCH /admin/categories/{catId}: catId={}", catId);

    return categoryService.update(catId, categoryDto);
  }

  @PostMapping("/compilations")
  @ResponseStatus(HttpStatus.CREATED)
  public CompilationDto createCompilation(@RequestBody @Valid NewCompilationDto compilationDto) {
    log.info("POST /admin/compilations: compilationDto={}", compilationDto);

    return compilationService.createNewCompilation(compilationDto);
  }

  @DeleteMapping("/compilation/{compId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public ResponseEntity<Object> removeCompilation(@RequestParam Long compId) {
    log.info("DELETE /admin/compilation/{compId}: compId={}", compId);

    return compilationService.deleteCompilation(compId);
  }

  @PatchMapping("/compilation/{compId}")
  public CompilationDto updateCompilation(
      @RequestBody @Valid UpdateCompilationRequestDto compilationUpdateDto,
      @PathVariable Long compId) {
    log.info(
        "PATCH /admin/compilation/{compId}: compilationUpdateDto={}, compId={}",
        compilationUpdateDto,
        compId);

    return compilationService.updateCompilation(compilationUpdateDto, compId);
  }

  @PatchMapping("/events/{eventId}")
  public EventFullDto updateEvent(
      @RequestBody @Valid UpdateEventAdminRequestDto updateDto, @PathVariable Long eventId) {
    log.info("PATCH /admin/events/{eventId}: updateDto={}, eventId={}", updateDto, eventId);

    return eventService.updateEventByAdmin(updateDto, eventId);
  }

  @GetMapping("/events")
  public List<EventFullDto> findEvents(
      @RequestParam(required = false) Long[] users,
      @RequestParam(required = false) String[] states,
      @RequestParam(required = false) Long[] categories,
      @RequestParam(required = false) String rangeStart,
      @RequestParam(required = false) String rangeEnd,
      @RequestParam(defaultValue = "0") Integer from,
      @RequestParam(defaultValue = "10") Integer size,
      HttpServletRequest request) {
    log.info(
        "GET /admin/events: user={}, states={}, categories={}, rangeStart={}, rangeEnd={}, from={}, size={}",
        users,
        states,
        categories,
        rangeStart,
        rangeEnd,
        from,
        size);
    Pageable pageable = getPageable(from, size);

    return eventService.findAllEventsWithFiltersAdmin(
        users, states, categories, rangeStart, rangeEnd, request, pageable);
  }
}
