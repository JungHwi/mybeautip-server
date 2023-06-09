package com.jocoos.mybeautip.domain.broadcast.vo;

import com.jocoos.mybeautip.client.flipfloplite.dto.PinMessageInfo;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastCategoryResponse;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastCategory;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastPinMessage;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastStatistics;
import com.jocoos.mybeautip.domain.member.dto.SimpleMemberInfo;
import com.jocoos.mybeautip.member.Member;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
public class BroadcastSearchResult {

    private final long id;
    private final long videoKey;
    private final String chatChannelKey;
    private final BroadcastStatus status;
    private final String url;
    private final String title;
    private final String notice;
    private final String thumbnailUrl;
    private final Boolean canChat;
    private final Boolean isSoundOn;
    private final Boolean isScreenShow;
    private final int viewerCount;
    private final int maxViewerCount;
    private final int heartCount;
    private final ZonedDateTime startedAt;
    private final ZonedDateTime endedAt;
    private final ZonedDateTime createdAt;
    private final BroadcastCategoryResponse category;
    private final SimpleMemberInfo createdBy;
    private final PinMessageInfo pinMessage;

    @QueryProjection
    public BroadcastSearchResult(Broadcast broadcast,
                                 BroadcastStatistics statistics,
                                 BroadcastCategory category,
                                 Member member,
                                 BroadcastPinMessage pinMessage) {
        this.id = broadcast.getId();
        this.videoKey = broadcast.getVideoKey();
        this.chatChannelKey = broadcast.getChatChannelKey();
        this.status = broadcast.getStatus();
        this.url = broadcast.getUrl();
        this.title = broadcast.getTitle();
        this.notice = broadcast.getNotice();
        this.thumbnailUrl = broadcast.getThumbnailUrl();
        this.canChat = broadcast.getCanChat();
        this.isSoundOn = broadcast.getIsSoundOn();
        this.isScreenShow = broadcast.getIsScreenShow();
        this.viewerCount = statistics.getViewerCount();
        this.maxViewerCount = statistics.getMaxViewerCount();
        this.heartCount = statistics.getHeartCount();
        this.startedAt = broadcast.getStartedAt();
        this.createdAt = broadcast.getCreatedAt();
        this.endedAt = broadcast.getEndedAt();
        this.category = new BroadcastCategoryResponse(category);
        this.createdBy = new SimpleMemberInfo(member);
        this.pinMessage = PinMessageInfo.pin(pinMessage);
    }
}
