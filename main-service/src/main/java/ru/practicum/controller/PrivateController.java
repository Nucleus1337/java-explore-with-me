package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.EventRequestDto;
import ru.practicum.dto.EventResponseDto;
import ru.practicum.service.EventService;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class PrivateController {
  private final EventService eventService;

  @PostMapping("/{userId}/events")
  @ResponseStatus(HttpStatus.CREATED)
  public EventResponseDto createEvent(
      @RequestBody @Valid EventRequestDto eventRequestDto, @PathVariable Long userId) {
    log.info("POST /{userId}/events: eventRequestDto={}, userId={}", eventRequestDto, userId);

    return eventService.createEvent(eventRequestDto, userId);
  }
}
