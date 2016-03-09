package com.agroknow.domain.validation;

import java.util.Map;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang.StringUtils;

public class NotEmptyKeyValidator implements ConstraintValidator<NotEmptyKey, Map<String, ?>> {

    public void initialize(NotEmptyKey constraintAnnotation) {
    }

    public boolean isValid(Map<String, ?> map, ConstraintValidatorContext context) {
        for(String key : map.keySet()) {
            if(StringUtils.isBlank(key))
                return false;
        }
        return true;
    }

}
