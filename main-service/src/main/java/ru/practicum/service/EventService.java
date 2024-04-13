package ru.practicum.service;

import static ru.practicum.mapper.EventMapper.toModel;
import static ru.practicum.mapper.EventMapper.toResponseFullDto;
import static ru.practicum.model.enums.StateActionEvent.PUBLISH_EVENT;
import static ru.practicum.model.enums.StateActionEvent.REJECT_EVENT;
import static ru.practicum.util.DateUtil.toLocalDateTime;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewEventDto;
import ru.practicum.dto.UpdateEventAdminRequestDto;
import ru.practicum.dto.UpdateEventUserRequestDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.exception.CustomException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.model.User;
import ru.practicum.model.enums.EventState;
import ru.practicum.model.enums.StateActionEvent;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.ParticipationRequestRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.util.DateUtil;

@Service
@RequiredArgsConstructor
public class EventService {
  private final EventRepository eventRepository;
  private final UserRepository userRepository;
  private final CategoryRepository categoryRepository;
  private final ParticipationRequestRepository participationRequestRepository;
  private final StatsClient statsClient;
  private final ObjectMapper objectMapper;

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

  private Long getParticipationRequestCountByEventId(Long eventId) {
    return participationRequestRepository.countByEventId(eventId);
  }

  private Long getParticipationRequestCountByEvent(Event event) {
    return participationRequestRepository.countByEvent(event);
  }

  private void checkIsUserOwner(Event event, Long userId) {
    if (!event.getUser().getId().equals(userId)) {
      throw new CustomException.UserException(
          String.format("Пользователь с id=%s не является владельцем события", userId));
    }
  }

  private void addStatistics(String serverName, String uri, String remoteAddress) {
    EndpointHitDto hit =
        new EndpointHitDto(serverName, uri, remoteAddress, DateUtil.toString(LocalDateTime.now()));

    statsClient.addHit(hit);
  }

  @Transactional
  public EventFullDto createEvent(NewEventDto newEventDto, Long userId) {
    User user = getUser(userId);
    Category category = getCategory(newEventDto.getCategory());

    Event event = toModel(newEventDto, category, user);

    return toResponseFullDto(eventRepository.saveAndFlush(event), 0L, 0L);
  }

  public List<EventShortDto> findUserEvents(Long userId, Pageable pageable) {
    User user = getUser(userId);

    List<Event> events =
        eventRepository.findByUserOrderByCreated(user, pageable).orElseGet(Collections::emptyList);
    List<ParticipationRequest> requests = participationRequestRepository.findAllByEvent(events);

    String[] uris = getUrisForEvents(events);

    List<ViewStatsDto> views = getViews(uris, DateUtil.toString(events.get(0).getCreated()));

    return getEventShortDtoList(events, requests, views);
  }

  public EventFullDto findUserEvent(Long userId, Long eventId) {
    Event event = getEvent(eventId);
    Long confirmedRequestsCount = getParticipationRequestCountByEvent(event);
    Long hits = getHits(new String[] {"/events/" + event.getId()}, event);

    checkIsUserOwner(event, userId);

    return toResponseFullDto(event, confirmedRequestsCount, hits);
  }

  @Transactional
  public EventFullDto updateEventByOwner(
      Long userId, Long eventId, UpdateEventUserRequestDto updateDto) {
    Event event = getEvent(eventId);
    Long confirmedRequestsCount = getParticipationRequestCountByEvent(event);

    checkIsUserOwner(event, userId);

    if (updateDto.getAnnotation() != null) {
      event.setAnnotation(updateDto.getAnnotation());
    }
    if (updateDto.getPaid() != null) {
      event.setPaid(updateDto.getPaid());
    }
    if (updateDto.getRequestModeration() != null) {
      event.setRequestModeration(updateDto.getRequestModeration());
    }
    if (updateDto.getLocation() != null) {
      if (updateDto.getLocation().get("lat") != null) {
        event.setLat((Double) updateDto.getLocation().get("lat"));
      }
      if (updateDto.getLocation().get("lon") != null) {
        event.setLon((Double) updateDto.getLocation().get("lon"));
      }
    }
    if (updateDto.getCategory() != null) {
      Category category = getCategory(updateDto.getCategory());
      event.setCategory(category);
    }
    if (updateDto.getEventDate() != null) {
      if (!LocalDateTime.now().plusHours(1).isBefore(toLocalDateTime(updateDto.getEventDate()))) {
        throw new CustomException.EventException("До начала должно быть хотя бы 2 часа");
      }
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

    Long hits = getHits(new String[] {"/events/" + event.getId()}, event);

    eventRepository.saveAndFlush(event);

    return toResponseFullDto(event, confirmedRequestsCount, hits);
  }

  private EventState getEventState(StateActionEvent actionEvent) {
    switch (actionEvent) {
      case PUBLISH_EVENT:
        return EventState.PUBLISHED;
      case REJECT_EVENT:
        return EventState.CANCELED;
      default:
        return null;
    }
  }

  @Transactional
  public EventFullDto updateEventByAdmin(UpdateEventAdminRequestDto updateDto, Long eventId) {
    Event event = getEvent(eventId);

    if (updateDto.getStateAction() != null) {
      if (updateDto.getStateAction().equalsIgnoreCase(PUBLISH_EVENT.toString())
          && !event.getState().equals(EventState.PENDING)) {
        throw new CustomException.EventConflictException("Event is already published");
      }
      if (updateDto.getStateAction().equalsIgnoreCase(REJECT_EVENT.toString())
          && !event.getState().equals(EventState.PUBLISHED)) {
        throw new CustomException.EventConflictException("Event is not published yet");
      }

      event.setState(getEventState(StateActionEvent.findByValue(updateDto.getStateAction())));
    }

    if (updateDto.getEventDate() != null) {
      LocalDateTime newEventDate = toLocalDateTime(updateDto.getEventDate());

      if (LocalDateTime.now().plusHours(1).isAfter(newEventDate)) {
        throw new CustomException.EventException("It should be at least 1 hour before start");
      }
      event.setEventDate(toLocalDateTime(updateDto.getEventDate()));
    }

    if (updateDto.getAnnotation() != null) {
      event.setAnnotation(updateDto.getAnnotation());
    }
    if (updateDto.getPaid() != null) {
      event.setPaid(updateDto.getPaid());
    }
    if (updateDto.getRequestModeration() != null) {
      event.setRequestModeration(updateDto.getRequestModeration());
    }
    if (updateDto.getLocation() != null) {
      if (updateDto.getLocation().get("lat") != null) {
        event.setLat((Double) updateDto.getLocation().get("lat"));
      }
      if (updateDto.getLocation().get("lon") != null) {
        event.setLon((Double) updateDto.getLocation().get("lon"));
      }
    }
    if (updateDto.getCategory() != null) {
      Category category = getCategory(updateDto.getCategory());
      event.setCategory(category);
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

    Long confirmedRequestsCount = getParticipationRequestCountByEvent(event);
    Long hits = getHits(new String[] {"/events/" + event.getId()}, event);

    eventRepository.saveAndFlush(event);

    return toResponseFullDto(event, confirmedRequestsCount, hits);
  }

  public EventFullDto findEventById(Long id, String serverName, String uri, String remoteAddress) {
    Event event = getEvent(id);

    if (event.getState() != EventState.PUBLISHED) {
      throw new CustomException.EventNotFoundException(
          String.format("Event with id=%s was not published", id));
    }

    addStatistics(serverName, uri, remoteAddress);

    Long confirmedRequestsCount = getParticipationRequestCountByEventId(id);
    Long hits = getHits(new String[] {uri}, event);

    return toResponseFullDto(event, confirmedRequestsCount, hits);
  }

  private Long getHits(String[] uris, Event event) {
    List<ViewStatsDto> views = getViews(uris, DateUtil.toString(event.getCreated()));

    return views.isEmpty() ? 0L : views.get(0).getHits();
  }

  @SuppressWarnings("unchecked")
  private List<ViewStatsDto> getViews(String[] uris, String startDate) {
    ResponseEntity<Object> response =
        statsClient.findStatistics(startDate, DateUtil.toString(LocalDateTime.now()), uris, true);

    List<ViewStatsDto> views = new ArrayList<>();
    for (Object o : (List<Object>) Objects.requireNonNull(response.getBody())) {
      views.add(objectMapper.convertValue(o, ViewStatsDto.class));
    }

    return views;
  }

  public List<EventShortDto> findAllEventsWithFilters(
      String text,
      List<Long> categories,
      Boolean paid,
      String rangeStart,
      String rangeEnd,
      Boolean onlyAvailable,
      HttpServletRequest request,
      Pageable pageable) {
    if (toLocalDateTime(rangeEnd).isBefore(toLocalDateTime(rangeStart))) {
      throw new CustomException.EventException("Дата окончания периода раньше его начала");
    }

    List<Category> categoryList = categoryRepository.findAllById(categories);
    if (categoryList.size() != categories.size()) {
      throw new CustomException.EventException("Найдены не все категории");
    }

    List<Event> events =
        eventRepository.findAllWithFilters(
            text,
            paid,
            categories,
            rangeStart,
            rangeEnd,
            onlyAvailable,
            pageable);
    List<ParticipationRequest> requests = participationRequestRepository.findAllByEvent(events);

    addStatistics(request.getServerName(), request.getRequestURI(), request.getRemoteAddr());

    String[] uris = getUrisForEvents(events);

    LocalDateTime startViewsFrom =
        events.stream()
            .map(Event::getEventDate)
            .min(LocalDateTime::compareTo)
            .orElse(LocalDateTime.now());

    List<ViewStatsDto> views = getViews(uris, DateUtil.toString(startViewsFrom));

    return getEventShortDtoList(events, requests, views);
  }

  private String[] getUrisForEvents(List<Event> events) {
    String[] uris = new String[events.size()];
    for (int i = 0; i < events.size(); i++) {
      uris[i] = "/events/" + events.get(i).getId();
    }

    return uris;
  }

  private List<EventShortDto> getEventShortDtoList(
      List<Event> events, List<ParticipationRequest> requests, List<ViewStatsDto> views) {
    return events.stream()
        .map(
            event -> {
              Long count =
                  requests.stream().filter(request -> request.getEvent().equals(event)).count();
              List<ViewStatsDto> viewsForEvent =
                  views.stream()
                      .filter(view -> view.getUri().equals("/events/" + event.getId()))
                      .collect(Collectors.toList());
              return EventMapper.toResponseShortDto(
                  event, count, viewsForEvent.isEmpty() ? 0L : viewsForEvent.get(0).getHits());
            })
        .collect(Collectors.toList());
  }

  private List<EventFullDto> getEventFullDtoList(
      List<Event> events, List<ParticipationRequest> requests, List<ViewStatsDto> views) {
    return events.stream()
        .map(
            event -> {
              Long count =
                  requests.stream().filter(request -> request.getEvent().equals(event)).count();
              List<ViewStatsDto> viewsForEvent =
                  views.stream()
                      .filter(view -> view.getUri().equals("/events/" + event.getId()))
                      .collect(Collectors.toList());
              return EventMapper.toResponseFullDto(
                  event, count, viewsForEvent.isEmpty() ? 0L : viewsForEvent.get(0).getHits());
            })
        .collect(Collectors.toList());
  }

  public List<EventFullDto> findAllEventsWithFiltersAdmin(
      List<Long> users,
      List<String> states,
      List<Long> categories,
      String rangeStart,
      String rangeEnd,
      HttpServletRequest request,
      Pageable pageable) {
    LocalDateTime start = toLocalDateTime(rangeStart);
    LocalDateTime end = toLocalDateTime(rangeEnd);

    List<Event> events =
        eventRepository.findAllWithFilters(
            users == null ? Collections.emptyList() : users,
            states == null ? Collections.emptyList() : states,
            categories == null ? Collections.emptyList() : categories,
            rangeStart,
            rangeEnd,
            pageable);
    List<ParticipationRequest> requests = participationRequestRepository.findAllByEvent(events);

    String[] uris = getUrisForEvents(events);

    LocalDateTime startViewsFrom =
        events.stream()
            .map(Event::getEventDate)
            .min(LocalDateTime::compareTo)
            .orElse(LocalDateTime.now());

    List<ViewStatsDto> views = getViews(uris, DateUtil.toString(startViewsFrom));

    return getEventFullDtoList(events, requests, views);
  }
}
