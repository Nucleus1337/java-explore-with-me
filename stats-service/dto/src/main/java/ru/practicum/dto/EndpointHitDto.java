package ru.practicum.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EndpointHitDto {
  @NotBlank private String app;
  @NotBlank private String uri;

  @NotBlank
  private String ip;

  @NotBlank private String timestamp;
}
