package com.jocoos.mybeautip.domain.vod.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastCategoryResponse;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastCategory;
import com.jocoos.mybeautip.domain.member.dto.SimpleMemberInfo;
import com.jocoos.mybeautip.domain.vod.persistence.domain.Vod;
import com.jocoos.mybeautip.global.wrapper.CursorInterface;
import com.jocoos.mybeautip.member.Member;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;

@Getter
public class VodListResponse implements CursorInterface {
    private final long id;
    private final Boolean isVisible;
    private final String url;
    private final String title;
    private final String thumbnailUrl;
    private final int viewCount;
    private final int heartCount;
    private final SimpleMemberInfo member;
    private final BroadcastCategoryResponse category;
    @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT) private final ZonedDateTime transcodeAt;
    @Setter private VodRelationInfo relationInfo;

    @QueryProjection
    public VodListResponse(Vod vod, BroadcastCategory category, Member member) {
        this.id = vod.getId();
        this.isVisible = vod.isVisible();
        this.url = vod.getUrl();
        this.title = vod.getTitle();
        this.thumbnailUrl = vod.getThumbnailUrl();
        this.viewCount = vod.getViewCount();
        this.heartCount = vod.getTotalHeartCount();
        this.transcodeAt = vod.getTranscodeAt();
        this.member = new SimpleMemberInfo(member);
        this.category = new BroadcastCategoryResponse(category);
    }

    @JsonIgnore
    @Override
    public String getCursor() {
        return String.valueOf(id);
    }
}
