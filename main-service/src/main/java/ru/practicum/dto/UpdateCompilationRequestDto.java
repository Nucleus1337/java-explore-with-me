package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class UpdateCompilationRequestDto {
  private List<Long> events;
  private Boolean pinned;

  @Length(min = 1, max = 50)
  private String title;
}
