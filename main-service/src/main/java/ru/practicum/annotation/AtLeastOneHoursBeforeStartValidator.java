package ru.practicum.annotation;

import static ru.practicum.util.DateUtil.toLocalDateTime;

import java.time.LocalDateTime;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AtLeastOneHoursBeforeStartValidator
    implements ConstraintValidator<AtLeastOneHoursBeforeStart, String> {

  @Override
  public void initialize(AtLeastOneHoursBeforeStart constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
  }

  @Override
  public boolean isValid(String string, ConstraintValidatorContext constraintValidatorContext) {
    LocalDateTime eventDate = toLocalDateTime(string);
    return LocalDateTime.now().plusHours(1).isBefore(eventDate);
  }
}
