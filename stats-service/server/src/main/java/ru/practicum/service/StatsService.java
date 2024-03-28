package ru.practicum.service;

import static ru.practicum.model.Mapper.toModel;
import static ru.practicum.util.DateUtil.toLocalDateTime;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.model.Hit;
import ru.practicum.repository.StatsRepository;

@Service
@RequiredArgsConstructor
public class StatsService {
  private final StatsRepository statsRepository;

  public void save(EndpointHitDto hitDto) {
    Hit hit = toModel(hitDto);
    statsRepository.saveAndFlush(hit);
  }

  public List<ViewStatsDto> find(
      String startString, String endString, String[] uris, boolean unique) {
    LocalDateTime start = toLocalDateTime(startString);
    LocalDateTime end = toLocalDateTime(endString);
    List<ViewStatsDto> resultDtos;

    if (uris == null) {
      if (unique) {
        resultDtos = statsRepository.findAllUnique(start, end);
      } else {
        resultDtos = statsRepository.findAll(start, end);
      }
    } else {
      if (unique) {
        resultDtos = statsRepository.findAllUniqueInUris(start, end, uris);
      } else {
        resultDtos = statsRepository.findAllInUris(start, end, uris);
      }
    }

    return resultDtos;
  }
}
