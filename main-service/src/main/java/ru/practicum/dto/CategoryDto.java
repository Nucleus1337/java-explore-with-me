package ru.practicum.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Builder
@Data
@AllArgsConstructor
public class CategoryDto {
  private Long id;

  @NotBlank
  @Length(min = 1, max = 50)
  private String name;
}
