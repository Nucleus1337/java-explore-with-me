package ru.practicum.service;

import static ru.practicum.mapper.ParticipationRequestMapper.toDto;
import static ru.practicum.model.ParticipationRequestStatus.CONFIRMED;
import static ru.practicum.model.ParticipationRequestStatus.PENDING;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.exception.CustomException;
import ru.practicum.model.Event;
import ru.practicum.model.EventState;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.model.User;
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

  public ParticipationRequestDto createParticipationRequest(Long userId, Long eventId) {
    Event event = getEvent(eventId);
    Long eventParticipantCount = participationRequestRepository.countByEvent(event);

    if (event.getUser().getId().equals(userId)
        || !event.getState().equals(EventState.PUBLISHED)
        || event.getParticipantLimit().equals(eventParticipantCount)) {
      throw new CustomException.ParticipantRequestConflictException(
          "Нельзя подавать заявку на свое же мероприятие");
    }

    User user = getUser(userId);

    ParticipationRequest participationRequest =
        ParticipationRequest.builder()
            .created(LocalDateTime.now())
            .user(user)
            .event(event)
            .status(event.getRequestModeration() ? PENDING : CONFIRMED)
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
    return toDto(participationRequest);
  }
}
