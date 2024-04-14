package ru.practicum.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@Builder
public class UserDto {
  private Long id;

  @NotBlank(message = "Field: name. Error: must not be blank. Value: null")
  @Length(min = 2, max = 250)
  private String name;

  @NotBlank(message = "Field: email. Error: must not be blank. Value: null")
  @Email
  @Length(min = 6, max = 254)
  private String email;
}
