package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.UniqueElements;

@Data
@AllArgsConstructor
public class NewCompilationDto {
  @UniqueElements private Long[] events;

  @JsonSetter(nulls = Nulls.SKIP)
  private Boolean pinned = false;

  @Length(min = 1, max = 50)
  @NotBlank
  private String title;
}
