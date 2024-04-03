package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import java.time.LocalDateTime;
import java.util.Map;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@AllArgsConstructor
@Builder
public class EventRequestDto {
  @NotBlank
  @Length(min = 20, max = 2000)
  private String annotation;

  @NotNull private Long category;

  @NotBlank
  @Length(min = 20, max = 7000)
  private String description;

  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  // TODO: сделать аннатацию, для проверки, что currentDateTime + 2H <= eventDate
  private LocalDateTime eventDate;

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
