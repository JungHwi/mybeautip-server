package com.jocoos.mybeautip.domain.broadcast.service;

import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastDao;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastViewerDao;
import com.jocoos.mybeautip.domain.member.service.dao.MemberDao;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.jocoos.mybeautip.global.exception.ErrorCode.ACCESS_DENIED;

@RequiredArgsConstructor
@Service
public class BroadcastChatService {

    private final MemberDao memberDao;
    private final BroadcastDao broadcastDao;
    private final BroadcastViewerDao viewerDao;
    private final BroadcastFFLService fflService;

    @Transactional
    public void sendChangeChatStatus(Long broadcastId, Long requestMemberId, boolean canChat) {
        Broadcast broadcast = broadcastDao.get(broadcastId);
        validAuthorization(broadcast, requestMemberId);
        broadcast.changeChatStatus(canChat);
        fflService.sendChangeChatStatusMessage(broadcast.getVideoKey(), canChat);
    }

    private void validAuthorization(Broadcast broadcast, Long requestMemberId) {
        if (!broadcast.isCreatedByEq(requestMemberId)
                && !viewerDao.isManager(broadcast.getId(), requestMemberId)
                && !memberDao.isAdmin(requestMemberId)) {
            throw new BadRequestException(ACCESS_DENIED, "not a admin or owner or manager of this broadcast");
        }
    }

    public void pinChat(Long broadcastId, Long chatId) {
        // TODO Pin Logic
    }
}
