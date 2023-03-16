package com.jocoos.mybeautip.domain.broadcast.service;

import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.service.child.BroadcastFFLService;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastDao;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastPermissionDao;
import com.jocoos.mybeautip.global.dto.IdAndBooleanResponse.CanChatResponse;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.jocoos.mybeautip.global.exception.ErrorCode.ACCESS_DENIED;

@RequiredArgsConstructor
@Service
public class BroadcastMessageService {

    private final BroadcastPermissionDao permissionDao;
    private final BroadcastDao broadcastDao;
    private final BroadcastFFLService fflService;

    @Transactional
    public CanChatResponse sendChangeMessageRoomStatus(Long broadcastId, Long requestMemberId, boolean canChat) {
        Broadcast broadcast = broadcastDao.get(broadcastId);
        validAuthorization(broadcast, requestMemberId);
        broadcast.changeMessageRoomStatus(canChat);
        fflService.sendChangeMessageRoomStatusMessage(broadcast.getVideoKey(), canChat);
        return new CanChatResponse(broadcast.getId(), broadcast.getCanChat());
    }

    private void validAuthorization(Broadcast broadcast, Long requestMemberId) {
        if (!broadcast.isCreatedByEq(requestMemberId)
                && !permissionDao.isManagerOrAdmin(broadcast.getId(), requestMemberId)) {
            throw new BadRequestException(ACCESS_DENIED, "not a admin or owner or manager of this broadcast");
        }
    }

    public void pinChat(Long broadcastId, Long chatId) {
        // TODO Pin Logic
    }
}
