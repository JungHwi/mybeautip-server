package com.jocoos.mybeautip.domain.vod.dto;

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

@Getter
public class VodResponse implements CursorInterface {
    private final long id;
    private final long videoKey;
    private final String url;
    private final String title;
    private final String thumbnailUrl;
    private final int viewCount;
    private final int heartCount;
    private final SimpleMemberInfo member;
    private BroadcastCategoryResponse category;

    @Setter
    private VodRelationInfo relationInfo;

    public VodResponse(Vod vod, Member member) {
        this.id = vod.getId();
        this.videoKey = vod.getVideoKey();
        this.url = vod.getUrl();
        this.title = vod.getTitle();
        this.thumbnailUrl = vod.getThumbnailUrl();
        this.viewCount = vod.getViewCount();
        this.heartCount = vod.getTotalHeartCount();
        this.member = new SimpleMemberInfo(member);
    }

    @QueryProjection
    public VodResponse(Vod vod, BroadcastCategory category, Member member) {
        this(vod, member);
        this.category = new BroadcastCategoryResponse(category);
    }

    @JsonIgnore
    @Override
    public String getCursor() {
        return String.valueOf(id);
    }
}
