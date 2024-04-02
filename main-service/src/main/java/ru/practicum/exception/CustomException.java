package ru.practicum.exception;

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
}
