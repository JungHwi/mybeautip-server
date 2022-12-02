package com.jocoos.mybeautip.domain.search.valid;

import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class SearchKeywordValidator implements ConstraintValidator<KeywordConstraint, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return StringUtils.isNotBlank(value) && value.length() <= 20;
    }
}
