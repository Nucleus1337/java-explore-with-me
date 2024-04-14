package ru.practicum.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = AtLeastTwoHoursBeforeStartValidator.class)
public @interface AtLeastTwoHoursBeforeStart {
  String message() default "До даты начала должно быть хотя бы 2 часа";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
