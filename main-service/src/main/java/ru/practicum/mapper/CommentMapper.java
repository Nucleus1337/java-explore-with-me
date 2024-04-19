package ru.practicum.mapper;

import java.time.LocalDateTime;
import lombok.experimental.UtilityClass;
import ru.practicum.dto.CommentRequestDto;
import ru.practicum.dto.CommentResponseDto;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.model.User;
import ru.practicum.util.DateUtil;

@UtilityClass
public class CommentMapper {
  public static Comment toModel(CommentRequestDto commentDto, User user, Event event) {
    return Comment.builder()
        .comment(commentDto.getComment())
        .user(user)
        .event(event)
        .created(LocalDateTime.now())
        .build();
  }

  public static CommentResponseDto toDto(Comment comment) {
    return CommentResponseDto.builder()
        .comment(comment.getComment())
        .userId(comment.getUser().getId())
        .eventId(comment.getEvent().getId())
        .created(DateUtil.toString(comment.getCreated()))
        .build();
  }

  public static CommentResponseDto toUpdatedDto(Comment comment) {
    return CommentResponseDto.builder()
        .comment(comment.getComment())
        .userId(comment.getUser().getId())
        .eventId(comment.getEvent().getId())
        .created(DateUtil.toString(comment.getCreated()))
        .updatedBy(comment.getUpdatedBy().toString())
        .updated(DateUtil.toString(comment.getUpdated()))
        .build();
  }
}
