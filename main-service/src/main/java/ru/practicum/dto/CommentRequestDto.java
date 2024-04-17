package ru.practicum.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequestDto {
  @Length(min = 5, max = 7000)
  @NotBlank
  private String comment;
}
