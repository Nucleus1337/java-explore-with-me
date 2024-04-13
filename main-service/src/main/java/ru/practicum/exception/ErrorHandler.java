package ru.practicum.exception;

import java.time.LocalDateTime;
import java.util.Objects;
import javax.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.util.DateUtil;

@ControllerAdvice
@Slf4j
public class ErrorHandler {

  @ExceptionHandler({MethodArgumentNotValidException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  ErrorResponse getBadRequestExceptionResponse(MethodArgumentNotValidException e) {
    log.error("Bad request: {}", e.getMessage());

    return new ErrorResponse(
        "BAD_REQUEST",
        "Incorrectly made request.",
        Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage(),
        DateUtil.toString(LocalDateTime.now()));
  }

  @ExceptionHandler({
    CustomException.UserException.class,
    CustomException.CategoryException.class,
    CustomException.EventException.class,
    IllegalStateException.class,
    MissingServletRequestParameterException.class
  })
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  ErrorResponse getBadRequestExceptionResponse(Exception e) {
    log.error("Bad reqeust: {}", e.getMessage());
    return new ErrorResponse(
        "BAD_REQUEST",
        "Incorrectly made request.",
        e.getLocalizedMessage(),
        DateUtil.toString(LocalDateTime.now()));
  }

  @ExceptionHandler({
    DataIntegrityViolationException.class,
    ConstraintViolationException.class,
    CustomException.ParticipantRequestConflictException.class,
    CustomException.EventConflictException.class,
    CustomException.ParticipantRequestConflictException.class
  })
  @ResponseStatus(HttpStatus.CONFLICT)
  @ResponseBody
  ErrorResponse getConflictExceptionResponse(Exception e) {
    log.error("Conflict: {}", e.getMessage());

    return new ErrorResponse(
        "CONFLICT",
        "Integrity constraint has been violated.",
        e.getLocalizedMessage(),
        DateUtil.toString(LocalDateTime.now()));
  }

  @ExceptionHandler({
    CustomException.UserNotFoundException.class,
    CustomException.CategoryNotFoundException.class,
    CustomException.EventNotFoundException.class
  })
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ResponseBody
  ErrorResponse getNotFoundExceptionResponse(Exception e) {
    log.error("Not found: {}", e.getMessage());
    return new ErrorResponse(
        "NOT_FOUND",
        "The required object was not found.",
        e.getLocalizedMessage(),
        DateUtil.toString(LocalDateTime.now()));
  }

  @ExceptionHandler({Exception.class})
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  ErrorResponse getRuntimeExceptionResponse(Exception e) {
    log.error("Internal Server Error: {}", e.getMessage());

    return new ErrorResponse(
        "INTERNAL_SERVER_ERROR",
        "Some kind of server error.",
        e.getLocalizedMessage(),
        DateUtil.toString(LocalDateTime.now()));
  }
}
