package ru.practicum.model;

import static ru.practicum.util.DateUtil.toLocalDateTime;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.EndpointHitDto;

@UtilityClass
public class Mapper {
  public static Hit toModel(EndpointHitDto dto) {
    return Hit.builder()
        .app(dto.getApp())
        .uri(dto.getUri())
        .ip(dto.getIp())
        .created(toLocalDateTime(dto.getTimestamp()))
        .build();
  }
}
