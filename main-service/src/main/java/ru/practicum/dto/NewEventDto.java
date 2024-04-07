package ru.practicum.dto;

import java.util.Map;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Value;
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

  @Value("false")
  private Boolean paid = false;

  @Value("0")
  @Positive
  private Long participantLimit = 0L;

  @Value("true")
  private Boolean requestModeration = true;

  @NotBlank
  @Length(min = 3, max = 120)
  private String title;
}
