package ru.practicum.service;

import static ru.practicum.mapper.CompilationMapper.toDto;
import static ru.practicum.mapper.CompilationMapper.toModel;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequestDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.exception.CustomException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.ParticipationRequestRepository;
import ru.practicum.util.DateUtil;

@Service
@RequiredArgsConstructor
public class CompilationService {
  private final CompilationRepository compilationRepository;
  private final EventRepository eventRepository;
  private final ParticipationRequestRepository participationRequestRepository;
  private final StatsClient statsClient;
  private final ObjectMapper objectMapper;

  private Compilation getCompilation(Long compId) {
    return compilationRepository
        .findById(compId)
        .orElseThrow(
            () ->
                new CustomException.CompilationNotFoundException(
                    String.format("Compilation with id=%s does not exist", compId)));
  }

  private List<EventShortDto> getEventsShort(List<Event> events) {
    if (events.isEmpty()) return Collections.emptyList();

    List<ParticipationRequest> requests = participationRequestRepository.findAllByEvents(events);

    String[] uris = new String[events.size()];
    for (int i = 0; i < events.size(); i++) {
      uris[i] = "/events/" + events.get(i).getId();
    }

    List<ViewStatsDto> views = getViews(uris, DateUtil.toString(events.get(0).getCreated()));

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

  private Long getHits(String[] uris, Event event) {
    List<ViewStatsDto> views = getViews(uris, DateUtil.toString(event.getCreated()));

    return views.isEmpty() ? 0L : views.get(0).getHits();
  }

  @SuppressWarnings("unchecked")
  private List<ViewStatsDto> getViews(String[] uris, String startDate) {
    ResponseEntity<Object> response =
        statsClient.findStatistics(startDate, DateUtil.toString(LocalDateTime.now()), uris, false);

    List<ViewStatsDto> views = new ArrayList<>();
    for (Object o : (List<Object>) Objects.requireNonNull(response.getBody())) {
      views.add(objectMapper.convertValue(o, ViewStatsDto.class));
    }

    return views;
  }

  public CompilationDto createNewCompilation(NewCompilationDto newCompilationDto) {
    Compilation compilation = compilationRepository.saveAndFlush(toModel(newCompilationDto));

    List<Event> events = new ArrayList<>();
    if (newCompilationDto.getEvents() != null && !newCompilationDto.getEvents().isEmpty()) {
      events = eventRepository.findAllById(newCompilationDto.getEvents());
      events.forEach(event -> event.setCompilation(compilation));

      eventRepository.saveAllAndFlush(events);
    }

    List<EventShortDto> eventsShort = getEventsShort(events);

    return toDto(compilation, eventsShort);
  }

  public ResponseEntity<Object> deleteCompilation(Long compId) {
    compilationRepository.delete(getCompilation(compId));

    return ResponseEntity.noContent().build();
  }

  public CompilationDto updateCompilation(UpdateCompilationRequestDto updateDto, Long compId) {
    List<Event> events;
    List<EventShortDto> eventsShort = Collections.emptyList();

    Compilation compilation = getCompilation(compId);

    if (updateDto.getPinned() != null) {
      compilation.setPinned(updateDto.getPinned());
    }
    if (updateDto.getTitle() != null) {
      compilation.setTitle(updateDto.getTitle());
    }
    if (updateDto.getEvents() != null) {
      events = eventRepository.findAllById(updateDto.getEvents());
      events.forEach(event -> event.setCompilation(compilation));

      eventsShort = getEventsShort(events);

      eventRepository.saveAll(events);
    }

    compilationRepository.saveAndFlush(compilation);

    return toDto(compilation, eventsShort);
  }

  public List<CompilationDto> findCompilations(Boolean pinned, Pageable pageable) {
    List<Compilation> compilations =
        compilationRepository
            .findAllCompilation(pinned, pageable)
            .orElseGet(Collections::emptyList);

    if (compilations.isEmpty()) {
      return Collections.emptyList();
    }

    List<Event> events = eventRepository.findAllByCompilation(compilations);

    return compilations.stream()
        .map(
            compilation ->
                toDto(
                    compilation,
                    getEventsShort(
                        events.stream()
                            .filter(event -> event.getCompilation().equals(compilation))
                            .collect(Collectors.toList()))))
        .collect(Collectors.toList());
  }

  public CompilationDto findCompilationById(Long compId) {
    Compilation compilation = getCompilation(compId);

    return toDto(compilation, getEventsShort(eventRepository.findByCompilation(compilation)));
  }
}
