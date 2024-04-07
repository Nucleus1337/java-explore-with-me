package ru.practicum.mapper;

import static ru.practicum.mapper.CategoryMapper.toDto;
import static ru.practicum.mapper.UserMapper.toShortDto;
import static ru.practicum.util.DateUtil.toLocalDateTime;

import java.time.LocalDateTime;
import lombok.experimental.UtilityClass;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.Location;
import ru.practicum.dto.NewEventDto;
import ru.practicum.dto.UserShortDto;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.EventState;
import ru.practicum.model.User;
import ru.practicum.util.DateUtil;

@UtilityClass
public class EventMapper {
  public static Event toModel(NewEventDto newEventDto, Category category, User user) {
    return Event.builder()
        .annotation(newEventDto.getAnnotation())
        .category(category)
        .description(newEventDto.getDescription())
        .eventDate(toLocalDateTime(newEventDto.getEventDate()))
        .lat((Double) newEventDto.getLocation().get("lat"))
        .lon((Double) newEventDto.getLocation().get("lon"))
        .paid(newEventDto.getPaid())
        .participantLimit(newEventDto.getParticipantLimit())
        .requestModeration(newEventDto.getRequestModeration())
        .title(newEventDto.getTitle())
        .created(LocalDateTime.now())
        .user(user)
        .state(EventState.PENDING)
        .build();
  }

  public static EventFullDto toResponseFullDto(Event event, Long confirmedRequestsCount) {
    UserShortDto userShortDto = toShortDto(event.getUser());
    CategoryDto categoryDto = toDto(event.getCategory());
    Location location = Location.builder().lat(event.getLat()).lon(event.getLon()).build();

    return EventFullDto.builder()
        .annotation(event.getAnnotation())
        .category(categoryDto)
        .confirmedRequests(confirmedRequestsCount)
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

  public static EventShortDto toResponseShortDto(Event event, Long confirmedRequestsCount) {
    CategoryDto categoryDto = toDto(event.getCategory());
    UserShortDto userShortDto = toShortDto(event.getUser());

    return EventShortDto.builder()
        .annotation(event.getAnnotation())
        .category(categoryDto)
        .confirmedRequests(confirmedRequestsCount)
        .eventDate(DateUtil.toString(event.getEventDate()))
        .initiator(userShortDto)
        .paid(event.getPaid())
        .title(event.getTitle())
        .views(0L) // TODO: по всей видимости сюда надо будет передавать статистику из stats-server
        .build();
  }
}
