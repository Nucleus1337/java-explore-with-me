package ru.practicum.service;

import static ru.practicum.mapper.EventMapper.toModel;
import static ru.practicum.mapper.EventMapper.toResponseDto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EventRequestDto;
import ru.practicum.dto.EventResponseDto;
import ru.practicum.exception.CustomException;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.User;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class EventService {
  private final EventRepository eventRepository;
  private final UserRepository userRepository;
  private final CategoryRepository categoryRepository;

  private User getUser(Long userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(
            () ->
                new CustomException.UserNotFoundException(
                    String.format("User with id=%s was not found", userId)));
  }

  private Category getCategory(Long categoryId) {
    return categoryRepository
        .findById(categoryId)
        .orElseThrow(
            () ->
                new CustomException.CategoryNotFoundException(
                    String.format("Category with id=%s was not found", categoryId)));
  }

  @Transactional
  public EventResponseDto createEvent(EventRequestDto eventRequestDto, Long userId) {
    User user = getUser(userId);
    Category category = getCategory(eventRequestDto.getCategory());

    Event event = toModel(eventRequestDto, category, user);

    return toResponseDto(eventRepository.saveAndFlush(event), category, user);
  }
}
