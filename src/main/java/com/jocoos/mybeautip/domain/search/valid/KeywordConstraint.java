package com.jocoos.mybeautip.domain.search.valid;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = SearchKeywordValidator.class)
@Target( { METHOD, FIELD, PARAMETER })
@Retention(RUNTIME)
public @interface KeywordConstraint {
    String message() default "Search Keyword Length Must 0 Or More And 20 Or Less";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
