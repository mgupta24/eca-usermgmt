package com.eca.usermgmt.validator;

import com.eca.usermgmt.constraint.PhoneNumber;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

@Component
public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber,Long> {
	private static final Pattern PATTERN = Pattern.compile("^\\d{10}$");
	@Override
	public boolean isValid(Long value, ConstraintValidatorContext context) {
		return value !=null && PATTERN.matcher(String.valueOf(value)).matches();
	}
}
