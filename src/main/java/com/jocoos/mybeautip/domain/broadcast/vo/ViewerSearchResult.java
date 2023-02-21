package com.jocoos.mybeautip.domain.broadcast.vo;

import com.jocoos.mybeautip.domain.broadcast.code.BroadcastViewerStatus;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastViewerType;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastViewer;
import com.jocoos.mybeautip.domain.broadcast.service.util.ViewerUsernameUtil;
import com.jocoos.mybeautip.member.Member;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.global.constant.MybeautipConstant.DEFAULT_AVATAR_URL;

@Getter
@RequiredArgsConstructor
public class ViewerSearchResult {

    private final BroadcastViewerType type;
    private final BroadcastViewerStatus status;
    private final Long memberId;
    private final String username;
    private final String avatarUrl;
    private final boolean isSuspended;
    private final ZonedDateTime suspendedAt;
    private final ZonedDateTime joinedAt;

    @QueryProjection
    public ViewerSearchResult(Member member, BroadcastViewer viewer) {
        this.type = viewer.getType();
        this.status = viewer.getStatus();
        this.memberId = viewer.getMemberId();
        this.username = member != null ? member.getUsername() : ViewerUsernameUtil.generateUsername(viewer.getSortedUsername());
        this.avatarUrl = member != null ? member.getAvatarUrl() : DEFAULT_AVATAR_URL;
        this.isSuspended = viewer.isSuspended();
        this.suspendedAt = viewer.getSuspendedAt();
        this.joinedAt = viewer.getJoinedAt();
    }
}
