package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import java.util.Map;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ru.practicum.annotation.AtLeastTwoHoursBeforeStart;

@Data
@AllArgsConstructor
@Builder
public class NewEventDto {
  @NotBlank
  @Length(min = 20, max = 2000)
  private String annotation;

  @NotNull private Long category;

  @NotBlank
  @Length(min = 20, max = 7000)
  private String description;

  @NotBlank
  @AtLeastTwoHoursBeforeStart
  private String eventDate;

  @NotEmpty private Map<String, Object> location;

  @JsonSetter(nulls = Nulls.SKIP)
  private Boolean paid = false;

  @JsonSetter(nulls = Nulls.SKIP)
  private Integer participantLimit = 0;

  @JsonSetter(nulls = Nulls.SKIP)
  private Boolean requestModeration = true;

  @NotBlank
  @Length(min = 3, max = 120)
  private String title;
}
