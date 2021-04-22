package com.campaign.subscription.constraint.validator;

import com.campaign.subscription.constraint.EnumConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.of;

public class EnumValidatorImpl implements ConstraintValidator<EnumConstraint, String> {

    List<String> valueList = null;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value != null) {
            return valueList.contains(value.toUpperCase());
        }
        return false;
    }

    @Override
    public void initialize(EnumConstraint constraintAnnotation) {
        valueList = of(constraintAnnotation.enumClass()
                .getEnumConstants()).map(Object::toString).collect(toList());
    }

}
