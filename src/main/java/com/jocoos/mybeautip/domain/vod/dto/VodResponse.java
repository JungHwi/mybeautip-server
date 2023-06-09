package com.jocoos.mybeautip.domain.vod.dto;

import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastKey;
import com.jocoos.mybeautip.domain.member.dto.SimpleMemberInfo;
import com.jocoos.mybeautip.domain.vod.persistence.domain.Vod;
import com.jocoos.mybeautip.member.Member;
import lombok.Getter;

import javax.annotation.Nullable;

@Getter
public class VodResponse {
    private final long id;
    private final String url;
    private final String title;
    private final String notice;
    private final String thumbnailUrl;
    private final int viewCount;
    private final int heartCount;
    private final SimpleMemberInfo member;
    private final BroadcastKey vodKey;
    private final VodRelationInfo relationInfo;

    public VodResponse(Vod vod,
                       Member member,
                       @Nullable BroadcastKey vodKey,
                       @Nullable VodRelationInfo relationInfo) {
        this.id = vod.getId();
        this.url = vod.getUrl();
        this.title = vod.getTitle();
        this.notice = vod.getNotice();
        this.thumbnailUrl = vod.getThumbnailUrl();
        this.viewCount = vod.getViewCount();
        this.heartCount = vod.getTotalHeartCount();
        this.member = new SimpleMemberInfo(member);
        this.vodKey = vodKey;
        this.relationInfo = relationInfo;
    }
}
