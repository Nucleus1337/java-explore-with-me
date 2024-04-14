package ru.practicum.exception;

import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.util.DateUtil;

@ControllerAdvice
@Slf4j
public class ErrorHandler {

  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler({
    CustomException.BadRequestException.class,
    MissingServletRequestParameterException.class
  })
  public ErrorResponse getBadRequestException(Exception e) {
    log.error("Bad Request: {}", e.getLocalizedMessage());

    return ErrorResponse.builder()
        .status("BAD_REQUEST")
        .reason("Wrong arguments in request")
        .message(e.getLocalizedMessage())
        .timestamp(DateUtil.toString(LocalDateTime.now()))
        .build();
  }

  @ExceptionHandler({Exception.class})
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  ErrorResponse getRuntimeExceptionResponse(Exception e) {
    log.error("Internal Server Error: {}", e.getMessage());

    return ErrorResponse.builder()
        .status("INTERNAL_SERVER_ERROR")
        .reason("Some kind of server error.")
        .message(e.getLocalizedMessage())
        .timestamp(DateUtil.toString(LocalDateTime.now()))
        .build();
  }
}
