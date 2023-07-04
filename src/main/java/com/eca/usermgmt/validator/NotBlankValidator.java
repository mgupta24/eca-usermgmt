package com.eca.usermgmt.validator;

import com.eca.usermgmt.constraint.NotBlank;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

@Component
public class NotBlankValidator implements ConstraintValidator<NotBlank, LocalDateTime> {

    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        return value !=null;
    }
}
