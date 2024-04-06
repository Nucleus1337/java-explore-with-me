package ru.practicum.service;

import static ru.practicum.mapper.CompilationMapper.toDto;
import static ru.practicum.mapper.CompilationMapper.toModel;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;

@Service
@RequiredArgsConstructor
public class CompilationService {
  private final CompilationRepository compilationRepository;
  private final EventRepository eventRepository;

  public CompilationDto createNewCompilation(NewCompilationDto newCompilationDto) {
    Compilation compilation = compilationRepository.saveAndFlush(toModel(newCompilationDto));

    List<Event> events = eventRepository.findAllById(newCompilationDto.getEvents());
    events.forEach(event -> event.setCompilation(compilation));

    eventRepository.saveAllAndFlush(events);

    List<EventShortDto> eventsShort =
        events.stream().map(EventMapper::toResponseShortDto).collect(Collectors.toList());

    return toDto(compilation, eventsShort);
  }
}
