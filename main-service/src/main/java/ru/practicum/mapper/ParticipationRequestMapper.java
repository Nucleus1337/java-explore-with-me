package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.model.ParticipationRequest;

@UtilityClass
public class ParticipationRequestMapper {
  public static ParticipationRequestDto toDto(ParticipationRequest participationRequest) {
    return ParticipationRequestDto.builder()
        .id(participationRequest.getId())
        .status(participationRequest.getStatus().toString())
        .created(participationRequest.getCreated())
        .requester(participationRequest.getUser().getId())
        .event(participationRequest.getEvent().getId())
        .build();
  }
}
