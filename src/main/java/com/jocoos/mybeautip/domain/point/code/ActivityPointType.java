package com.jocoos.mybeautip.domain.point.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


@Getter
@RequiredArgsConstructor
public enum ActivityPointType implements CodeValue {

    INPUT_ADDITIONAL_INFO("나의 정보 입력", 100, 0),
    INPUT_EXTRA_INFO("추가 정보 입력", 100, 0),

    WRITE_COMMUNITY("게시물 작성", 50, 25),
    WRITE_PHOTO_COMMUNITY("포토 게시물 작성", 100, 50),
//    WRITE_COMMENT("댓글 작성", 2, 1),

    WRITE_VIDEO_COMMENT("비디오 댓글 작성", 2, 1),
    WRITE_COMMUNITY_COMMENT("게시글 댓글 작성", 2, 1),

    GET_LIKE_COMMUNITY("게시글 좋아요 획득", 2, 0),
    GET_LIKE_COMMUNITY_COMMENT("게시글 댓글 좋아요 획득", 2, 0),
    GET_LIKE_VIDEO("비디오 좋아요 획득", 2, 0),
    GET_LIKE_VIDEO_COMMENT("비디오 댓글 좋아요 획득", 2, 0),

    VIDEO_LIKE("영상 콘텐츠 좋아요", 10, 5),
    VIDEO_SCRAP("영상 콘텐츠 스크랩", 10, 5);

    private final String description;
    private final int givenPoint;
    private final int retrievePoint;

    private static final ActivityPointType[] ACTIVITY_POINT_TYPES_VALUES = ActivityPointType.values();

    public static final Set<ActivityPointType> WRITE_COMMUNITY_TYPES =
            Collections.unmodifiableSet(new HashSet<>(Arrays.asList(WRITE_COMMUNITY, WRITE_PHOTO_COMMUNITY)));

    public static final Set<ActivityPointType> WRITE_COMMENT_TYPES =
            Collections.unmodifiableSet(new HashSet<>(Arrays.asList(WRITE_COMMUNITY_COMMENT, WRITE_VIDEO_COMMENT)));

    public static ActivityPointType getActivityPointType(int ordinal) {
        return ACTIVITY_POINT_TYPES_VALUES[ordinal];
    }


    @Override
    public String getName() {
        return this.name();
    }
}


