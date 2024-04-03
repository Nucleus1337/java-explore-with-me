package ru.practicum.mapper;

import static ru.practicum.mapper.CategoryMapper.toDto;
import static ru.practicum.mapper.UserMapper.toShortDto;

import java.time.LocalDateTime;
import lombok.experimental.UtilityClass;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.EventRequestDto;
import ru.practicum.dto.EventResponseDto;
import ru.practicum.dto.Location;
import ru.practicum.dto.UserShortDto;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.EventState;
import ru.practicum.model.User;
import ru.practicum.util.DateUtil;

@UtilityClass
public class EventMapper {
  public static Event toModel(EventRequestDto eventRequestDto, Category category, User user) {
    return Event.builder()
        .annotation(eventRequestDto.getAnnotation())
        .category(category)
        .description(eventRequestDto.getDescription())
        .eventDate(eventRequestDto.getEventDate())
        .lat((Float) eventRequestDto.getLocation().get("lat"))
        .lon((Float) eventRequestDto.getLocation().get("lon"))
        .paid(eventRequestDto.getPaid())
        .participantLimit(eventRequestDto.getParticipantLimit())
        .requestModeration(eventRequestDto.getRequestModeration())
        .title(eventRequestDto.getTitle())
        .created(LocalDateTime.now())
        .user(user)
        .state(EventState.PENDING)
        .build();
  }

  public static EventResponseDto toResponseDto(Event event, Category category, User user) {
    UserShortDto userShortDto = toShortDto(user);
    CategoryDto categoryDto = toDto(category);
    Location location = Location.builder().lat(event.getLat()).lon(event.getLon()).build();

    return EventResponseDto.builder()
        .annotation(event.getAnnotation())
        .category(categoryDto)
        .confirmedRequests(0L) // TODO: получать и прокидывать сюда подтвержденные запросы
        .createdOn(DateUtil.toString(event.getCreated()))
        .description(event.getDescription())
        .eventDate(DateUtil.toString(event.getEventDate()))
        .id(event.getId())
        .initiator(userShortDto)
        .location(location)
        .paid(event.getPaid())
        .participantLimit(event.getParticipantLimit())
        .publishedOn(DateUtil.toString(event.getPublished()))
        .state(event.getState().toString())
        .title(event.getTitle())
        .views(0L) // TODO: по всей видимости сюда надо будет передавать статистику из stats-server
        .build();
  }
}
