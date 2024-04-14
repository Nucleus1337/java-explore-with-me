package ru.practicum.model.enums;

public enum ParticipationRequestStatus {
  PENDING,
  CONFIRMED,
  REJECTED,
  CANCELED;

  public static ParticipationRequestStatus findByValue(String value) {
    ParticipationRequestStatus result = null;
    for (ParticipationRequestStatus status : values()) {
      if (status.name().equalsIgnoreCase(value)) {
        result = status;
        break;
      }
    }
    return result;
  }
}
