package com.jocoos.mybeautip.domain.broadcast.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastCategoryResponse;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastCategory;
import com.jocoos.mybeautip.domain.member.dto.SimpleMemberInfo;
import com.jocoos.mybeautip.member.Member;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;

@Getter
public class BroadcastSearchResult {

    private final long id;
    private final long videoKey;
    private final BroadcastStatus status;
    private final String url;
    private final String title;
    private final String notice;
    private final String thumbnailUrl;
    private final int viewerCount;
    private final int maxViewerCount;
    private final int heartCount;

    @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT)
    private final ZonedDateTime startedAt;

    @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT)
    private final ZonedDateTime createdAt;

    private final BroadcastCategoryResponse category;
    private final SimpleMemberInfo member;

    @QueryProjection
    public BroadcastSearchResult(Broadcast broadcast,
                                 BroadcastCategory category,
                                 Member member) {
        this.id = broadcast.getId();
        this.videoKey = broadcast.getVideoKey();
        this.status = broadcast.getStatus();
        this.url = broadcast.getUrl();
        this.title = broadcast.getTitle();
        this.notice = broadcast.getNotice();
        this.thumbnailUrl = broadcast.getThumbnailUrl();
        this.viewerCount = broadcast.getViewerCount();
        this.maxViewerCount = broadcast.getMaxViewerCount();
        this.heartCount = broadcast.getHeartCount();
        this.startedAt = broadcast.getStartedAt();
        this.createdAt = broadcast.getCreatedAt();
        this.category = new BroadcastCategoryResponse(category);
        this.member = new SimpleMemberInfo(member);
    }

}
