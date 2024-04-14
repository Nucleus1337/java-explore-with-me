package ru.practicum.controller;

import static ru.practicum.util.DateUtil.toLocalDateTime;

import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.exception.CustomException;
import ru.practicum.service.StatsService;

@RestController
@RequestMapping
@Slf4j
@RequiredArgsConstructor
public class StatsController {
  private final StatsService statsService;

  @PostMapping("/hit")
  public ResponseEntity<Object> saveEndpointStats(@RequestBody @Valid EndpointHitDto hitDto) {
    log.info("POST /hit: hitDto={}", hitDto);

    return statsService.save(hitDto);
  }

  @GetMapping("/stats")
  public List<ViewStatsDto> getStats(
      @RequestParam String start,
      @RequestParam String end,
      @RequestParam(required = false) String[] uris,
      @RequestParam(defaultValue = "false") boolean unique) {
    log.info("GET /stats: start={}, end={}, uris={}, unique={}", start, end, uris, unique);

    if (start.isBlank()) {
      throw new CustomException.BadRequestException("Start should not be blank");
    }

    if (end.isBlank()) {
      throw new CustomException.BadRequestException("End should not be blank");
    }

    if (toLocalDateTime(start).isAfter(toLocalDateTime(end))) {
      throw new CustomException.BadRequestException("End should be after start");
    }

    return statsService.find(start, end, uris, unique);
  }
}
