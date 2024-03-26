package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.service.StatsService;

@RestController
@RequestMapping(path = "/api/v1")
@Slf4j
@RequiredArgsConstructor
public class StatsController {
  private final StatsService statsService;

  @PostMapping("/hit")
  public Object saveEndpointStats() {
    return statsService.save();
  }

  @GetMapping("/stats")
  public Object getStats(
      @RequestParam String start,
      @RequestParam String end,
      @RequestParam(required = false) String[] uris,
      @RequestParam(defaultValue = "false") boolean unique) {
    return statsService.find();
  }
}
