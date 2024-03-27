package ru.practicum.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EndpointHitDto {
  @NotBlank private String app;
  @NotBlank private String uri;

  @NotBlank
  @Pattern(regexp = "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$")
  private String ip;

  @NotBlank private String timestamp;
}
