package ru.practicum.model;

import ru.practicum.dto.EndpointHitDto;

public class Mapper {
  public Hit requestDtoToModel(EndpointHitDto dto) {
    return Hit.builder()
        .app(dto.getApp())
        .uri(dto.getUri())
        .ip(dto.getIp())
        .created(dto.getTimestamp())
        .build();
  }
}
