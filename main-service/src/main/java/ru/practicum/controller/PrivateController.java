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
import ru.practicum.dto.CommentRequestDto;
import ru.practicum.dto.CommentResponseDto;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventRequestStatusUpdateRequestDto;
import ru.practicum.dto.EventRequestStatusUpdateResultDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewEventDto;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.dto.UpdateEventUserRequestDto;
import ru.practicum.exception.CustomException;
import ru.practicum.service.CommentService;
import ru.practicum.service.EventService;
import ru.practicum.service.ParticipationRequestService;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class PrivateController {
  private final EventService eventService;
  private final ParticipationRequestService participationRequestService;
  private final CommentService commentService;

  @PostMapping("/{userId}/events")
  @ResponseStatus(HttpStatus.CREATED)
  public EventFullDto createEvent(
      @RequestBody @Valid NewEventDto newEventDto, @PathVariable Long userId) {
    log.info("POST /users/{userId}/events: eventRequestDto={}, userId={}", newEventDto, userId);

    return eventService.createEvent(newEventDto, userId);
  }

  @GetMapping("/{userId}/events")
  public List<EventShortDto> findUserEvents(
      @PathVariable Long userId,
      @RequestParam(defaultValue = "0") Integer from,
      @RequestParam(defaultValue = "10") Integer size) {
    log.info("GET /users/{userId}/events: userId={}, from={}, size={}", userId, from, size);
    Pageable pageable = getPageable(from, size);

    return eventService.findUserEvents(userId, pageable);
  }

  @GetMapping("/{userId}/events/{eventId}")
  public EventFullDto findUserEvent(@PathVariable Long userId, @PathVariable Long eventId) {
    log.info("GET /users/{userId}/events/{eventId}: userId={}, eventId={}", userId, eventId);

    return eventService.findUserEvent(userId, eventId);
  }

  @PatchMapping("/{userId}/events/{eventId}")
  public EventFullDto updateUserEvent(
      @PathVariable Long userId,
      @PathVariable Long eventId,
      @RequestBody @Valid UpdateEventUserRequestDto updateDto) {
    log.info(
        "PATCH /users/{userId}/events/{eventId}: userId={}, eventId={}, updateDto={}",
        userId,
        eventId,
        updateDto);

    return eventService.updateEventByOwner(userId, eventId, updateDto);
  }

  @GetMapping("/{userId}/events/{eventId}/requests")
  public List<ParticipationRequestDto> findRequests(
      @PathVariable Long userId, @PathVariable Long eventId) {
    log.info(
        "GET /users/{userId}/events/{eventId}/requests: userId={}, eventId={}", userId, eventId);

    return participationRequestService.findAllParticipationRequestOnMyEventId(userId, eventId);
  }

  @PatchMapping("/{userId}/events/{eventId}/requests")
  public EventRequestStatusUpdateResultDto updateRequestStatuses(
      @PathVariable Long userId,
      @PathVariable Long eventId,
      @RequestBody EventRequestStatusUpdateRequestDto updateDto) {
    log.info(
        "PATCH /users/{userId}/events/{eventId}/requests: userId={}, eventId={}, updateDto={}",
        userId,
        eventId,
        updateDto);
    if (findByValue(updateDto.getStatus()) == null
        && findByValue(updateDto.getStatus()).equals(PENDING)) {
      throw new CustomException.ParticipantRequestException("Неверный статус");
    }
    return participationRequestService.changeStatusRequests(
        userId, eventId, updateDto.getRequestIds(), updateDto.getStatus());
  }

  @PostMapping("/{userId}/requests")
  @ResponseStatus(HttpStatus.CREATED)
  public ParticipationRequestDto createParticipationRequest(
      @PathVariable Long userId, @RequestParam Long eventId) {
    log.info("POST /users/{userId}/requests: userId={}, eventId={}", userId, eventId);

    return participationRequestService.createParticipationRequest(userId, eventId);
  }

  @PatchMapping("/{userId}/requests/{requestId}/cancel")
  public ParticipationRequestDto cancelParticipantRequest(
      @PathVariable Long userId, @PathVariable Long requestId) {
    log.info(
        "PATCH /users/{userId}/requests/{requestId}/cancel: userId={}, requestId={}",
        userId,
        requestId);

    return participationRequestService.cancelParticipantRequest(userId, requestId);
  }

  @GetMapping("/{userId}/requests")
  public List<ParticipationRequestDto> getUserRequests(@PathVariable Long userId) {
    log.info("GET /users/{userId}/requests: userId={}", userId);
    return participationRequestService.findUserRequests(userId);
  }

  @PostMapping("/{userId}/events/{eventId}/comments")
  @ResponseStatus(HttpStatus.CREATED)
  public CommentResponseDto create(
      @RequestBody @Valid CommentRequestDto commentDto,
      @PathVariable Long userId,
      @PathVariable Long eventId) {
    log.info(
        "PATCH /users/{userId}/events/{eventId/comment: commentDto={}, userId={}, eventId={}",
        commentDto,
        userId,
        eventId);

    return commentService.create(commentDto, userId, eventId);
  }

  @DeleteMapping("/{userId}/events/{eventId}/comments/{commentId}")
  public ResponseEntity<Object> deleteComment(
      @PathVariable Long userId, @PathVariable Long eventId, @PathVariable Long commentId) {
    log.info(
        "DELETE /users/{userId}/events/{eventId}/comments/{commentId}: userId={}, eventId={}, commentId={}",
        userId,
        eventId,
        commentId);

    return commentService.deleteByOwner(commentId, userId, eventId);
  }

  @PatchMapping("/{userId}/events/{eventId}/comments/{commentId}")
  public CommentResponseDto updateComment(
      @RequestBody @Valid CommentRequestDto commentRequestDto,
      @PathVariable Long userId,
      @PathVariable Long eventId,
      @PathVariable Long commentId) {
    log.info(
        "PATCH /users/{userId}/events/{eventId}/comments/{commentId}: "
            + "userId={}, eventId={}, commentId={}, commentRequestDto={}",
        userId,
        eventId,
        commentId,
        commentRequestDto);

    return commentService.updateByOwner(commentRequestDto, commentId, userId);
  }
}
