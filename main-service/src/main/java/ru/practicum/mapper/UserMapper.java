package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.UserDto;
import ru.practicum.dto.UserShortDto;
import ru.practicum.model.User;

@UtilityClass
public class UserMapper {
  public static User toModel(UserDto userDto) {
    return User.builder().name(userDto.getName()).email(userDto.getEmail()).build();
  }

  public static UserDto toDto(User user) {
    return UserDto.builder().id(user.getId()).name(user.getName()).email(user.getEmail()).build();
  }

  public static UserShortDto toShortDto(User user) {
    return UserShortDto.builder().id(user.getId()).name(user.getName()).build();
  }
}
