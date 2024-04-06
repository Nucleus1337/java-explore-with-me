package ru.practicum.mapper;

import java.util.List;
import lombok.experimental.UtilityClass;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.model.Compilation;

@UtilityClass
public class CompilationMapper {
  public static Compilation toModel(NewCompilationDto dto) {
    return Compilation.builder().title(dto.getTitle()).pinned(dto.getPinned()).build();
  }

  public static CompilationDto toDto(Compilation compilation, List<EventShortDto> events) {
    return CompilationDto.builder()
        .id(compilation.getId())
        .pinned(compilation.getPinned())
        .title(compilation.getTitle())
        .events(events)
        .build();
  }
}
