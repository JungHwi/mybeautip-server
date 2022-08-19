package com.jocoos.mybeautip.domain.point.service.activity;

@FunctionalInterface
public interface ActivityPointValidator {
    boolean valid(ValidObject validObject);
}
