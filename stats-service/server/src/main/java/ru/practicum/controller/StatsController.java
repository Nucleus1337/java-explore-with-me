package ru.practicum.controller;

import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
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
    statsService.save(hitDto);

    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @GetMapping("/stats")
  public List<ViewStatsDto> getStats(
      @RequestParam String start,
      @RequestParam String end,
      @RequestParam(required = false) String[] uris,
      @RequestParam(defaultValue = "false") boolean unique) {
    log.info("GET /stats: start={}, end={}, uris={}, unique={}", start, end, uris, unique);

    return statsService.find(start, end, uris, unique);
  }
}
