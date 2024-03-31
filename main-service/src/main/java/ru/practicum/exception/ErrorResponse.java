package ru.practicum.exception;

import lombok.Data;

@Data
public class ErrorResponse {
  private final String status;
  private final String reason;
  private final String message;
  private final String timestamp;
}
