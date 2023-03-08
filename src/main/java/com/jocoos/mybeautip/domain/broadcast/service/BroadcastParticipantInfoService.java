package com.jocoos.mybeautip.domain.broadcast.service;

import com.jocoos.mybeautip.client.flipfloplite.FlipFlopLiteService;
import com.jocoos.mybeautip.client.flipfloplite.dto.ChatTokenAndAppId;
import com.jocoos.mybeautip.domain.broadcast.converter.BroadcastViewerConverter;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastKey;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastParticipantInfo;
import com.jocoos.mybeautip.domain.broadcast.dto.ViewerResponse;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastViewerDao;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastSearchResult;
import com.jocoos.mybeautip.domain.member.service.dao.MemberDao;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.jocoos.mybeautip.domain.broadcast.code.BroadcastViewerType.*;

@RequiredArgsConstructor
@Service
public class BroadcastParticipantInfoService {

    private final MemberDao memberDao;
    private final BroadcastViewerDao viewerDao;
    private final BroadcastViewerConverter converter;
    private final FlipFlopLiteService flipFlopLiteService;

    @Transactional(readOnly = true)
    public BroadcastParticipantInfo getParticipantInfo(Long requestMemberId, BroadcastSearchResult searchResult) {
        Member member = memberDao.getMember(requestMemberId);
        Long ownerId = searchResult.getCreatedBy().getId();

        ViewerResponse viewerResponse = getViewerResponse(member, ownerId, searchResult.getId());
        ChatTokenAndAppId chatTokenAndAppId = flipFlopLiteService.getChatToken(requestMemberId);
        BroadcastKey broadcastKey = BroadcastKey.builder()
                .channelKey(searchResult.getChatChannelKey())
                .gossipToken(chatTokenAndAppId.chatToken())
                .appId(chatTokenAndAppId.appId())
                .streamKey(getStreamKey(requestMemberId, ownerId))
                .build();
        return new BroadcastParticipantInfo(viewerResponse, broadcastKey);
    }

    private ViewerResponse getViewerResponse(Member member, Long broadcastOwnerId, Long broadcastId) {
        if (member.isAdmin()) {
            return converter.converts(member, ADMIN);
        }
        if (broadcastOwnerId.equals(member.getId())) {
            return converter.converts(member, OWNER);
        }

        return viewerDao.findBroadcastViewer(broadcastId, member.getId())
                .map(converter::converts)
                .orElse(converter.converts(member, MEMBER));
    }

    private String getStreamKey(Long requestMemberId, Long broadcastOwnerId) {
        if (broadcastOwnerId.equals(requestMemberId)) {
            return flipFlopLiteService.getStreamKey(requestMemberId);
        }
        return null;
    }
}
