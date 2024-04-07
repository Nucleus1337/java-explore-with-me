package ru.practicum.service;

import static ru.practicum.mapper.CompilationMapper.toDto;
import static ru.practicum.mapper.CompilationMapper.toModel;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequestDto;
import ru.practicum.exception.CustomException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.ParticipationRequestRepository;

@Service
@RequiredArgsConstructor
public class CompilationService {
  private final CompilationRepository compilationRepository;
  private final EventRepository eventRepository;
  private final ParticipationRequestRepository participationRequestRepository;

  private Compilation getCompilation(Long compId) {
    return compilationRepository
        .findById(compId)
        .orElseThrow(
            () ->
                new CustomException.CompilationNotFoundException(
                    String.format("Compilation with id=%s does not exist", compId)));
  }

  private List<EventShortDto> getEventsShort(List<Event> events) {
    List<ParticipationRequest> requests = participationRequestRepository.findAllByEvent(events);
    return events.stream()
        .map(
            event -> {
              Long count =
                  requests.stream().filter(request -> request.getEvent().equals(event)).count();
              return EventMapper.toResponseShortDto(event, count);
            })
        .collect(Collectors.toList());
  }

  public CompilationDto createNewCompilation(NewCompilationDto newCompilationDto) {
    Compilation compilation = compilationRepository.saveAndFlush(toModel(newCompilationDto));

    List<Event> events = eventRepository.findAllById(newCompilationDto.getEvents());
    events.forEach(event -> event.setCompilation(compilation));

    eventRepository.saveAllAndFlush(events);

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
    if (!updateDto.getTitle().isBlank()) {
      compilation.setTitle(updateDto.getTitle());
    }
    if (updateDto.getEvents().length != 0) {
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
