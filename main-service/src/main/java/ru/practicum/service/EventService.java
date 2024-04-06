package ru.practicum.service;

import static ru.practicum.mapper.EventMapper.toModel;
import static ru.practicum.mapper.EventMapper.toResponseFullDto;
import static ru.practicum.util.DateUtil.toLocalDateTime;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewEventDto;
import ru.practicum.dto.UpdateEventUserRequestDto;
import ru.practicum.exception.CustomException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.User;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class EventService {
  private final EventRepository eventRepository;
  private final UserRepository userRepository;
  private final CategoryRepository categoryRepository;

  private User getUser(Long userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(
            () ->
                new CustomException.UserNotFoundException(
                    String.format("User with id=%s was not found", userId)));
  }

  private Category getCategory(Long categoryId) {
    return categoryRepository
        .findById(categoryId)
        .orElseThrow(
            () ->
                new CustomException.CategoryNotFoundException(
                    String.format("Category with id=%s was not found", categoryId)));
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

  public EventFullDto createEvent(NewEventDto newEventDto, Long userId) {
    User user = getUser(userId);
    Category category = getCategory(newEventDto.getCategory());

    Event event = toModel(newEventDto, category, user);

    return toResponseFullDto(eventRepository.saveAndFlush(event));
  }

  public List<EventShortDto> findUserEvents(Long userId, Pageable pageable) {
    User user = getUser(userId);

    List<Event> events = eventRepository.findByUser(user, pageable).orElseGet(Collections::emptyList);

    return events.stream().map(EventMapper::toResponseShortDto).collect(Collectors.toList());
  }

  public EventFullDto findUserEvent(Long userId, Long eventId) {
    Event event = getEvent(eventId);

    checkIsUserOwner(event, userId);

    return toResponseFullDto(event);
  }

  public EventFullDto updateUserEvent(
      Long userId, Long eventId, UpdateEventUserRequestDto updateDto) {
    Event event = getEvent(eventId);

    checkIsUserOwner(event, userId);

    Category category = getCategory(updateDto.getCategory());

    if (updateDto.getAnnotation() != null) {
      event.setAnnotation(updateDto.getAnnotation());
    }
    if (updateDto.getPaid() != null) {
      event.setPaid(updateDto.getPaid());
    }
    if (updateDto.getRequestModeration() != null) {
      event.setRequestModeration(updateDto.getRequestModeration());
    }
    if (!updateDto.getLocation().isEmpty()) {
      if (updateDto.getLocation().get("lat") != null) {
        event.setLat((Double) updateDto.getLocation().get("lat"));
      }
      if (updateDto.getLocation().get("lon") != null) {
        event.setLon((Double) updateDto.getLocation().get("lon"));
      }
    }
    if (updateDto.getCategory() != null) {
      event.setCategory(category);
    }
    if (!updateDto.getEventDate().isBlank()) {
      event.setEventDate(toLocalDateTime(updateDto.getEventDate()));
    }
    if (updateDto.getDescription() != null) {
      event.setDescription(updateDto.getDescription());
    }
    if (updateDto.getTitle() != null) {
      event.setTitle(updateDto.getTitle());
    }
    if (updateDto.getParticipantLimit() != null) {
      event.setParticipantLimit(updateDto.getParticipantLimit());
    }

    eventRepository.saveAndFlush(event);

    return toResponseFullDto(event);
  }


}
