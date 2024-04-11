package ru.practicum.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = AtLeastOneHoursBeforeStartValidator.class)
public @interface AtLeastOneHoursBeforeStart {
  String message() default "До даты начала должно быть хотя бы 1 час";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
