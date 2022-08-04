package com.jocoos.mybeautip.domain.point.code;

import com.jocoos.mybeautip.domain.point.util.DateRestriction;
import com.jocoos.mybeautip.domain.point.util.PerDomainRestriction;
import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.jocoos.mybeautip.domain.point.util.DateRestriction.*;
import static com.jocoos.mybeautip.domain.point.util.PerDomainRestriction.noPerDomainLimit;
import static com.jocoos.mybeautip.domain.point.util.PerDomainRestriction.oncePerDomain;


@Getter
@RequiredArgsConstructor
public enum ActivityPointType implements CodeValue {

    INPUT_ADDITIONAL_INFO("나의 정보 입력", 100, 0, allTimeOnce(), noPerDomainLimit()),
    INPUT_EXTRA_INFO("추가 정보 입력", 100, 0, allTimeOnce(), noPerDomainLimit()),

    WRITE_POST("게시물 작성", 50, 25, day(5), noPerDomainLimit()),
    WRITE_PHOTO_POST("포토 게시물 작성", 100, 50, day(5), noPerDomainLimit()),
    WRITE_COMMENT("댓글 작성", 2, 1, day(10), noPerDomainLimit()),

    GET_LIKE_POST("게시글 좋아요 획득", 2, 0, noDateLimit(), oncePerDomain()),
    GET_LIKE_COMMENT("댓글 좋아요 획득", 2, 0, noDateLimit(), oncePerDomain()),
    GET_LIKE_VIDEO("비디오 좋아요 획득", 2, 0, noDateLimit(), oncePerDomain()),

    VIDEO_LIKE("영상 콘텐츠 좋아요", 10, 5, day(5), oncePerDomain()),
    VIDEO_SCRAP("영상 콘텐츠 스크랩", 10, 5, day(2), oncePerDomain());


    private final String description;
    private final int givenPoint;
    private final int retrievePoint;
    private final DateRestriction dateRestriction;
    private final PerDomainRestriction perDomainRestriction;

    private static final ActivityPointType[] activityPointTypesValues = ActivityPointType.values();

    public static ActivityPointType getActivityPointType(int ordinal) {
        return activityPointTypesValues[ordinal];
    }

    public DateLimit getDateLimit() {
        return this.getDateRestriction().getDateLimit();
    }

    public long getDateLimitNum() {
        return this.getDateRestriction().getLimitNum();
    }

    public boolean isPerDomainRestrict() {
        return this.perDomainRestriction.isRestrict();
    }

    @Override
    public String getName() {
        return this.name();
    }
}


