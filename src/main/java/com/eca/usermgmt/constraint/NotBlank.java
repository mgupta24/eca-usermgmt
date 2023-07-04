package com.eca.usermgmt.constraint;

import com.eca.usermgmt.validator.NotBlankValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
@Documented
@Constraint(validatedBy = NotBlankValidator.class)
public @interface NotBlank {
    String message() default "Field cannot be null";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
