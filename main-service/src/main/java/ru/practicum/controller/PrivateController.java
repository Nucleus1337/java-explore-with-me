package ru.practicum.controller;

import static ru.practicum.model.enums.ParticipationRequestStatus.PENDING;
import static ru.practicum.model.enums.ParticipationRequestStatus.findByValue;
import static ru.practicum.util.Utils.getPageable;

import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventRequestStatusUpdateRequestDto;
import ru.practicum.dto.EventRequestStatusUpdateResultDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewEventDto;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.dto.UpdateEventUserRequestDto;
import ru.practicum.exception.CustomException;
import ru.practicum.service.EventService;
import ru.practicum.service.ParticipationRequestService;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class PrivateController {
  private final EventService eventService;
  private final ParticipationRequestService participationRequestService;

  @PostMapping("/{userId}/events")
  @ResponseStatus(HttpStatus.CREATED)
  public EventFullDto createEvent(
      @RequestBody @Valid NewEventDto newEventDto, @PathVariable Long userId) {
    log.info("POST /{userId}/events: eventRequestDto={}, userId={}", newEventDto, userId);

    return eventService.createEvent(newEventDto, userId);
  }

  @GetMapping("/{userId}/events")
  public List<EventShortDto> findUserEvents(
      @PathVariable Long userId,
      @RequestParam(defaultValue = "0") Integer from,
      @RequestParam(defaultValue = "10") Integer size) {
    log.info("GET /{userId}/events: userId={}, from={}, size={}", userId, from, size);
    Pageable pageable = getPageable(from, size);

    return eventService.findUserEvents(userId, pageable);
  }

  @GetMapping("/{userId}/events/{eventId}")
  public EventFullDto findUserEvent(@PathVariable Long userId, @PathVariable Long eventId) {
    log.info("GET /{userId}/events/{eventId}: userId={}, eventId={}", userId, eventId);

    return eventService.findUserEvent(userId, eventId);
  }

  @PatchMapping("/{userId}/events/{eventId}")
  public EventFullDto updateUserEvent(
      @PathVariable Long userId,
      @PathVariable Long eventId,
      @RequestBody @Valid UpdateEventUserRequestDto updateDto) {
    log.info("PATCH /{userId}/events/{eventId}: userId={}, eventId={}", userId, eventId);

    return eventService.updateEventByOwner(userId, eventId, updateDto);
  }

  @GetMapping("/{userId}/events/{eventId}/requests")
  public List<ParticipationRequestDto> findRequests(
      @PathVariable Long userId, @PathVariable Long eventId) {
    log.info("GET /{userId}/events/{eventId}/requests: userId={}, eventId={}", userId, eventId);

    return participationRequestService.findAllParticipationRequestOnMyEventId(userId, eventId);
  }

  @PatchMapping("/{userId}/events/{eventId}/requests")
  public EventRequestStatusUpdateResultDto updateRequestStatuses(
      @PathVariable Long userId,
      @PathVariable Long eventId,
      @RequestBody EventRequestStatusUpdateRequestDto updateDto) {
    log.info(
        "PATCH /{userId}/events/{eventId}/requests: userId={}, eventId={}, updateDto={}",
        userId,
        eventId,
        updateDto);
    if (findByValue(updateDto.getStatus()) == null
        && findByValue(updateDto.getStatus()).equals(PENDING)) {
      throw new CustomException.ParticipantRequestException("Неверный статус");
    }
    return participationRequestService.changeStatusToPendingRequests(
        userId, eventId, updateDto.getRequestIds(), updateDto.getStatus());
  }

  @PostMapping("/{userId}/requests")
  @ResponseStatus(HttpStatus.CREATED)
  public ParticipationRequestDto createParticipationRequest(
      @PathVariable Long userId, @RequestParam Long eventId) {
    log.info("POST /{userId}/requests: userId={}, eventId={}", userId, eventId);

    return participationRequestService.createParticipationRequest(userId, eventId);
  }

  @PatchMapping("/{userId}/requests/{requestId}/cancel")
  public ParticipationRequestDto cancelParticipantRequest(
      @PathVariable Long userId, @PathVariable Long requestId) {
    log.info(
        "PATCH /{userId}/requests/{requestId}/cancel: userId={}, requestId={}", userId, requestId);

    return participationRequestService.cancelParticipantRequest(userId, requestId);
  }
}
