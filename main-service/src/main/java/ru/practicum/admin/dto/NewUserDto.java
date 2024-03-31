package ru.practicum.admin.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NewUserDto {
  private Long id;

  @NotBlank(message = "Field: name. Error: must not be blank. Value: null")
  private String name;

  @NotBlank(message = "Field: email. Error: must not be blank. Value: null")
  @Email
  private String email;
}
