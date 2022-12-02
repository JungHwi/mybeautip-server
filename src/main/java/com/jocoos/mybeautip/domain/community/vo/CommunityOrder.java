package com.jocoos.mybeautip.domain.community.vo;

import com.querydsl.core.types.OrderSpecifier;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.domain.community.persistence.domain.QCommunity.community;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommunityOrder {

    private static final OrderSpecifier<ZonedDateTime> SORTED_AT_DESC = community.sortedAt.desc();
    private static final OrderSpecifier<Boolean> IS_WIN = community.isWin.desc().nullsLast();
    private static final OrderSpecifier<Boolean> IS_TOP_FIX = community.isTopFix.desc().nullsLast();


    public static OrderSpecifier<?>[] order(boolean useIsWin) {
        if (useIsWin) {
            return isWinAndSortedAt();
        }
        return isTopFixAndSortedAt();
    }

    public static OrderSpecifier<?>[] sortedAt() {
        return new OrderSpecifier[]{SORTED_AT_DESC};
    }

    private static OrderSpecifier<?>[] isWinAndSortedAt() {
        return new OrderSpecifier[]{IS_WIN, SORTED_AT_DESC};
    }

    private static OrderSpecifier<?>[] isTopFixAndSortedAt() {
        return new OrderSpecifier[]{IS_TOP_FIX, SORTED_AT_DESC};
    }
}
