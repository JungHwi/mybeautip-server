package com.jocoos.mybeautip.domain.event.persistence.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FixSorting {

    @Column
    private Integer sorting;

    @Column
    private Boolean isTopFix;

    private FixSorting(Integer sorting, boolean isTopFix) {
        this.sorting = sorting;
        this.isTopFix = isTopFix;
    }

    public static FixSorting fix(int sorting) {
        return new FixSorting(sorting, true);
    }

    public static FixSorting unFix() {
        return new FixSorting(null, false);
    }


}
