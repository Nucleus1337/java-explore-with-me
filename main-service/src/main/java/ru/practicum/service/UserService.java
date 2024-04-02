package ru.practicum.service;

import static ru.practicum.mapper.UserMapper.toDto;
import static ru.practicum.mapper.UserMapper.toModel;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.UserDto;
import ru.practicum.exception.CustomException;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;

  @Transactional
  public UserDto create(UserDto userDto) {
    User user = toModel(userDto);

    return toDto(userRepository.saveAndFlush(user));
  }

  public List<UserDto> findUsers(Integer[] ids, Pageable pageable) {
    if (ids == null) {
      return userRepository.findAll(pageable).stream()
          .map(UserMapper::toDto)
          .collect(Collectors.toList());
    }

    return userRepository.findUsersByIds(ids).orElseGet(Collections::emptyList);
  }

  @Transactional
  public ResponseEntity<Object> deleteUser(Long userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(
                () ->
                    new CustomException.UserNotFoundException(
                        String.format("User with id=%s was not found", userId)));

    userRepository.delete(user);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
