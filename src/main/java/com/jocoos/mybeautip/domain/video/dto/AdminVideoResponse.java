package com.jocoos.mybeautip.domain.video.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jocoos.mybeautip.domain.community.dto.MemberResponse;
import com.jocoos.mybeautip.domain.video.persistence.domain.VideoCategory;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.video.Video;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;

@Getter
@RequiredArgsConstructor
public class AdminVideoResponse {

    private final Long id;
    private final String visibility;
    private final String videoKey;
    private final String url;
    private final String thumbnailUrl;
    private final String title;
    private final String content;
    private final Boolean isTopFix;
    private final Boolean isRecommended;
    private final int viewCount;
    private final int likeCount;
    private final int commentCount;
    private final long reportCount;
    private final int duration;
    private final List<VideoCategoryResponse> category;
    private final MemberResponse member;

    @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT)
    private final ZonedDateTime createdAt;

    @QueryProjection
    public AdminVideoResponse(Video video, Member member, List<VideoCategory> categories) {
        this.id = video.getId();
        this.visibility = video.getVisibility();
        this.videoKey = video.getVideoKey();
        this.url = video.getUrl();
        this.thumbnailUrl = video.getThumbnailUrl();
        this.title = video.getTitle();
        this.content = video.getContent();
        this.isTopFix = video.isTopFixTrueOrNull();
        this.isRecommended = video.isRecommendedTrueOrNull();
        this.viewCount = video.getViewCount();
        this.likeCount = video.getLikeCount();
        this.commentCount = video.getCommentCount();
        this.reportCount = video.getReportCount();
        this.createdAt = video.getCreatedAtZoned();
        this.duration = video.getDuration();
        this.category = setCategory(categories);
        this.member = MemberResponse.from(member);
    }

    private List<VideoCategoryResponse> setCategory(List<VideoCategory> categories) {
        return categories.stream()
                .map(this::categoryIdAndTitle)
                .toList();
    }

    private VideoCategoryResponse categoryIdAndTitle(VideoCategory videoCategory) {
        return new VideoCategoryResponse(videoCategory.getId(), videoCategory.getTitle());
    }
}
