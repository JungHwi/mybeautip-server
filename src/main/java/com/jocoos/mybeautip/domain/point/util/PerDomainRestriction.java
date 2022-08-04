package com.jocoos.mybeautip.domain.point.util;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PerDomainRestriction {
    private final boolean isRestrict;

    public static PerDomainRestriction oncePerDomain() {
        return new PerDomainRestriction(true);
    }

    public static PerDomainRestriction noPerDomainLimit() {
        return new PerDomainRestriction(false);
    }
}
