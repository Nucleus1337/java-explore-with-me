package ru.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class CustomException {
  public static class UserException extends RuntimeException {
    public UserException(String message) {
      super(message);
    }
  }

  public static class UserNotFoundException extends UserException {
    public UserNotFoundException(String message) {
      super(message);
    }
  }

  public static class CategoryException extends RuntimeException {
    public CategoryException(String message) {
      super(message);
    }
  }

  public static class CategoryNotFoundException extends CategoryException {
    public CategoryNotFoundException(String message) {
      super(message);
    }
  }

  public static class EventException extends RuntimeException {
    public EventException(String message) {
      super(message);
    }
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  public static class EventNotFoundException extends EventException {
    public EventNotFoundException(String message) {
      super(message);
    }
  }
}
