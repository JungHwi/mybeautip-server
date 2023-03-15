package com.jocoos.mybeautip.domain.broadcast.service;

import com.jocoos.mybeautip.domain.broadcast.converter.BroadcastViewerConverter;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastKey;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastParticipantInfo;
import com.jocoos.mybeautip.domain.broadcast.dto.ViewerResponse;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastViewerDao;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastSearchResult;
import com.jocoos.mybeautip.domain.member.service.dao.MemberDao;
import com.jocoos.mybeautip.global.util.MemberUtil;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.jocoos.mybeautip.domain.broadcast.code.BroadcastViewerType.*;
import static com.jocoos.mybeautip.global.util.MemberUtil.isGuest;

@RequiredArgsConstructor
@Service
public class BroadcastParticipantInfoService {

    private final MemberDao memberDao;
    private final BroadcastViewerDao viewerDao;
    private final BroadcastViewerConverter converter;
    private final BroadcastFFLService fflService;

    @Transactional(readOnly = true)
    public BroadcastParticipantInfo getParticipantInfo(String requestUsername, BroadcastSearchResult searchResult) {
        Long ownerId = searchResult.getCreatedBy().getId();
        ViewerResponse viewerResponse = getViewerResponse(requestUsername, ownerId, searchResult.getId());
        BroadcastKey broadcastKey = getBroadcastKey(requestUsername, ownerId, searchResult.getChatChannelKey());
        return new BroadcastParticipantInfo(viewerResponse, broadcastKey);
    }

    private ViewerResponse getViewerResponse(String requestUsername, Long broadcastOwnerId, Long broadcastId) {

        if (isGuest(requestUsername)) {
            Long guestNameWithoutPrefix = MemberUtil.getGuestId(requestUsername);
            return viewerDao.findGuestViewer(broadcastId, guestNameWithoutPrefix)
                    .map(converter::converts)
                    .orElse(converter.toGuest(requestUsername));
        }

        long requestMemberId = Long.parseLong(requestUsername);
        Member member = memberDao.getMember(requestMemberId);

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

    private BroadcastKey getBroadcastKey(String requestUsername, Long ownerId, String channelKey) {
        if (isGuest(requestUsername)) {
            return fflService.getGuestBroadcastKey(requestUsername, channelKey);
        }

        long requestMemberId = Long.parseLong(requestUsername);
        if (isStreamKeyNeeded(requestMemberId, ownerId)) {
            return fflService.getBroadcastKeyWithStreamKey(requestMemberId, channelKey, ownerId);
        }
        return fflService.getBroadcastKey(requestMemberId, channelKey);
    }

    private boolean isStreamKeyNeeded(Long requestMemberId, Long broadcastOwnerId) {
        return broadcastOwnerId.equals(requestMemberId) || memberDao.isAdmin(requestMemberId);
    }
}
