package com.campaign.subscription.constraint;

import com.campaign.subscription.constants.ErrorConstants;
import com.campaign.subscription.constraint.validator.EnumValidatorImpl;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EnumValidatorImpl.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@NotNull(message = ErrorConstants.MISSING_STATE)
public @interface EnumConstraint {
    String[] acceptedValues();

    Class<? extends Enum<?>> enumClass();

    String message() default "State is not valid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
