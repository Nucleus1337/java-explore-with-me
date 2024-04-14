package ru.practicum.service;

import static ru.practicum.mapper.ParticipationRequestMapper.toDto;
import static ru.practicum.model.enums.ParticipationRequestStatus.CANCELED;
import static ru.practicum.model.enums.ParticipationRequestStatus.CONFIRMED;
import static ru.practicum.model.enums.ParticipationRequestStatus.PENDING;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.EventRequestStatusUpdateResultDto;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.exception.CustomException;
import ru.practicum.mapper.ParticipationRequestMapper;
import ru.practicum.model.Event;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.model.User;
import ru.practicum.model.enums.EventState;
import ru.practicum.model.enums.ParticipationRequestStatus;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.ParticipationRequestRepository;
import ru.practicum.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class ParticipationRequestService {
  private final ParticipationRequestRepository participationRequestRepository;
  private final UserRepository userRepository;
  private final EventRepository eventRepository;

  private User getUser(Long userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(
            () ->
                new CustomException.UserNotFoundException(
                    String.format("User with id=%s was not found", userId)));
  }

  private Event getEvent(Long eventId) {
    return eventRepository
        .findById(eventId)
        .orElseThrow(
            () ->
                new CustomException.EventNotFoundException(
                    String.format("Event with id=%s was not found", eventId)));
  }

  private void checkIsUserOwner(Event event, Long userId) {
    if (!event.getUser().getId().equals(userId)) {
      throw new CustomException.UserException(
          String.format("Пользователь с id=%s не является владельцем события", userId));
    }
  }

  public ParticipationRequestDto createParticipationRequest(Long userId, Long eventId) {
    Event event = getEvent(eventId);
    Long eventParticipantCount = participationRequestRepository.countByEvent(event);

    if (event.getUser().getId().equals(userId)) {
      throw new CustomException.ParticipantRequestConflictException(
          "Нельзя подавать заявку на свое же мероприятие");
    }
    if (!event.getState().equals(EventState.PUBLISHED)) {
      throw new CustomException.ParticipantRequestConflictException("Событие не опубликовано");
    }
    if (event.getParticipantLimit() != 0L
        && event.getParticipantLimit().equals(eventParticipantCount)) {
      throw new CustomException.ParticipantRequestConflictException("Исчерпан лимит заявок");
    }

    User user = getUser(userId);

    ParticipationRequestStatus status =
        !event.getRequestModeration() || event.getParticipantLimit() == 0L ? CONFIRMED : PENDING;

    ParticipationRequest participationRequest =
        ParticipationRequest.builder()
            .created(LocalDateTime.now())
            .user(user)
            .event(event)
            .status(status)
            .build();
    return toDto(participationRequestRepository.saveAndFlush(participationRequest));
  }

  public ParticipationRequestDto cancelParticipantRequest(Long userId, Long requestId) {
    ParticipationRequest participationRequest =
        participationRequestRepository
            .findById(requestId)
            .orElseThrow(
                () ->
                    new CustomException.ParticipantRequestNotFoundException(
                        String.format("Request with id=%s was not found", requestId)));
    if (!participationRequest.getUser().getId().equals(userId)) {
      throw new CustomException.ParticipantRequestNotFoundException(
          String.format("User with id=%s is not owner of request", userId));
    }

    participationRequest.setStatus(CANCELED);

    return toDto(participationRequestRepository.saveAndFlush(participationRequest));
  }

  public List<ParticipationRequestDto> findAllParticipationRequestOnMyEventId(
      Long userId, Long eventId) {
    Event event = getEvent(eventId);

    checkIsUserOwner(event, userId);

    return participationRequestRepository.findByEvent(event).stream()
        .map(ParticipationRequestMapper::toDto)
        .collect(Collectors.toList());
  }

  public EventRequestStatusUpdateResultDto changeStatusRequests(
      Long userId, Long eventId, List<Long> requestIds, String status) {
    Event event = getEvent(eventId);
    checkIsUserOwner(event, userId);

    if (event.getParticipantLimit().equals(0L) || !event.getRequestModeration()) {
      return new EventRequestStatusUpdateResultDto(
          Collections.emptyList(), Collections.emptyList());
    }

    requestIds.sort(null);

    List<ParticipationRequest> requests = participationRequestRepository.findByEvent(event);

    Long allConfirmed =
        requests.stream().filter(request -> request.getStatus().equals(CONFIRMED)).count();

    if (allConfirmed.equals(event.getParticipantLimit())) {
      throw new CustomException.ParticipantRequestConflictException("Достигнут лимит по заявкам");
    }

    List<ParticipationRequest> confirmed = new ArrayList<>();
    List<ParticipationRequest> rejected = new ArrayList<>();

    requests.forEach(
        request -> {
          if (requestIds.contains(request.getId()) && request.getStatus() != PENDING) {
            throw new CustomException.ParticipantRequestConflictException(
                    "статус можно менять только для завявок в ожидании");
          }
          if (requestIds.contains(request.getId()) && request.getStatus() == PENDING) {
            request.setStatus(ParticipationRequestStatus.valueOf(status));
          }

          if (confirmed.size() + allConfirmed <= event.getParticipantLimit()
              && request.getStatus() == CONFIRMED) {
            confirmed.add(request);
          } else {
            if (request.getStatus() != PENDING) {
              rejected.add(request);
            }
          }
        });

    participationRequestRepository.saveAllAndFlush(
        Stream.concat(confirmed.stream(), rejected.stream()).collect(Collectors.toList()));

    return new EventRequestStatusUpdateResultDto(
        confirmed.stream().map(ParticipationRequestMapper::toDto).collect(Collectors.toList()),
        rejected.stream().map(ParticipationRequestMapper::toDto).collect(Collectors.toList()));
  }

  public List<ParticipationRequestDto> findUserRequests(Long userId) {
    User user = getUser(userId);
    List<ParticipationRequest> requests = participationRequestRepository.findByUser(user);
    return requests.stream().map(ParticipationRequestMapper::toDto).collect(Collectors.toList());
  }
}
