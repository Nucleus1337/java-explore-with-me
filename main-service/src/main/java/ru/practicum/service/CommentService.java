package ru.practicum.service;

import static ru.practicum.mapper.CommentMapper.toDto;
import static ru.practicum.mapper.CommentMapper.toModel;
import static ru.practicum.mapper.CommentMapper.toUpdatedDto;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.CommentRequestDto;
import ru.practicum.dto.CommentResponseDto;
import ru.practicum.exception.CustomException;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.model.User;
import ru.practicum.model.enums.CommentUpdater;
import ru.practicum.model.enums.EventState;
import ru.practicum.model.enums.ParticipationRequestStatus;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.ParticipationRequestRepository;
import ru.practicum.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CommentService {
  private final CommentRepository commentRepository;
  private final UserRepository userRepository;
  private final EventRepository eventRepository;
  private final ParticipationRequestRepository requestRepository;

  private User getUser(Long userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(
            () ->
                new CustomException.UserNotFoundException(
                    String.format("User with id=%s was not found", userId)));
  }

  private Event getEvent(Long eventId) {
    return eventRepository
        .findById(eventId)
        .orElseThrow(
            () ->
                new CustomException.EventNotFoundException(
                    String.format("Event with id=%s was not found", eventId)));
  }

  private ParticipationRequest getRequest(User user, Event event) {
    return requestRepository
        .findByUserAndEvent(user, event)
        .orElseThrow(
            () ->
                new CustomException.ParticipantRequestNotFoundException(
                    String.format(
                        "Request for event with id=%s from user with id=%s was not found",
                        user.getId(), event.getId())));
  }

  private Comment getComment(Long commentId) {
    return commentRepository
        .findById(commentId)
        .orElseThrow(
            () ->
                new CustomException.CommentNotFoundException(
                    String.format("Comment with id=%s was not found", commentId)));
  }

  @Transactional
  public CommentResponseDto create(CommentRequestDto commentRequestDto, Long userId, Long eventId) {
    User user = getUser(userId);
    Event event = getEvent(eventId);
    ParticipationRequest request = getRequest(user, event);

    Comment otherComment = commentRepository.findByUserAndEvent(user, event).orElse(null);

    if (otherComment != null) {
      throw new CustomException.CommentConflictException("You may comment the event just once");
    }

    if (request.getStatus() != ParticipationRequestStatus.CONFIRMED) {
      throw new CustomException.CommentConflictException(
          "Only users with confirmed request may comment event");
    }

    if (event.getState() != EventState.PUBLISHED) {
      throw new CustomException.CommentConflictException("You may comment published events only");
    }

    if (event.getEventDate().isAfter(LocalDateTime.now())) {
      throw new CustomException.CommentConflictException(
          "You can comment event after its start only");
    }

    Comment comment = toModel(commentRequestDto, user, event);

    return toDto(commentRepository.saveAndFlush(comment));
  }

  @Transactional
  public ResponseEntity<Object> deleteByAdmin(Long commentId) {
    commentRepository.deleteById(commentId);

    return ResponseEntity.noContent().build();
  }

  @Transactional
  public ResponseEntity<Object> deleteByOwner(Long commentId, Long userId, Long eventId) {
    Comment comment = getComment(commentId);

    if (!comment.getUser().getId().equals(userId)) {
      throw new CustomException.CommentConflictException("Only the owner can delete comment");
    }

    if (!comment.getEvent().getId().equals(eventId)) {
      throw new CustomException.CommentConflictException("This comment from other event");
    }

    if (comment.getCreated().plusHours(12).isBefore(LocalDateTime.now())) {
      throw new CustomException.CommentConflictException("You cant delete comment after 12 hour");
    }

    commentRepository.delete(comment);

    return ResponseEntity.noContent().build();
  }

  @Transactional
  public CommentResponseDto updateByOwner(
      CommentRequestDto commentRequestDto, Long commentId, Long userId) {
    Comment comment = getComment(commentId);

    if (!comment.getUser().getId().equals(userId)) {
      throw new CustomException.CommentConflictException("Only the owner can update comment");
    }

    if (comment.getCreated().plusHours(12).isBefore(LocalDateTime.now())) {
      throw new CustomException.CommentConflictException("You cant update comment after 12 hour");
    }

    setFields(comment, commentRequestDto.getComment(), CommentUpdater.OWNER);

    return toUpdatedDto(commentRepository.saveAndFlush(comment));
  }

  @Transactional
  public CommentResponseDto updateByAdmin(CommentRequestDto commentRequestDto, Long commentId) {
    Comment comment = getComment(commentId);

    setFields(comment, commentRequestDto.getComment(), CommentUpdater.ADMIN);

    return toUpdatedDto(commentRepository.saveAndFlush(comment));
  }

  private void setFields(Comment comment, String newText, CommentUpdater updatedBy) {
    comment.setComment(newText);
    comment.setUpdated(LocalDateTime.now());
    comment.setUpdatedBy(updatedBy);
  }
}
