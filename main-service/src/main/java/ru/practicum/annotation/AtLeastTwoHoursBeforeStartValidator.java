package ru.practicum.annotation;

import static ru.practicum.util.DateUtil.toLocalDateTime;

import java.time.LocalDateTime;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AtLeastTwoHoursBeforeStartValidator
    implements ConstraintValidator<AtLeastTwoHoursBeforeStart, String> {

  @Override
  public void initialize(AtLeastTwoHoursBeforeStart constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
  }

  @Override
  public boolean isValid(String string, ConstraintValidatorContext constraintValidatorContext) {
    if (string == null) return true;

    LocalDateTime eventDate = toLocalDateTime(string);
    return LocalDateTime.now().plusHours(2).minusSeconds(5).isBefore(eventDate)
        || LocalDateTime.now().plusHours(2).minusSeconds(5).isEqual(eventDate);
  }
}
