package ru.practicum.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Data
@AllArgsConstructor
public class NewCompilationDto {
  private List<Long> events;

  private Boolean pinned;

  @Length(min = 1, max = 50)
  @NotBlank
  private String title;
}
