package com.jocoos.mybeautip.domain.broadcast.service;

import com.jocoos.mybeautip.client.flipfloplite.dto.PinMessageInfo;
import com.jocoos.mybeautip.domain.broadcast.converter.BroadcastPinChatConverter;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastPinChatResponse;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastPinMessageRequest;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastPinMessage;
import com.jocoos.mybeautip.domain.broadcast.service.child.BroadcastFFLService;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastDao;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastPermissionDao;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastPinChatDao;
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
    private final BroadcastPinChatDao pinChatDao;
    private final BroadcastFFLService fflService;
    private final BroadcastPinChatConverter converter;

    @Transactional
    public CanChatResponse sendChangeMessageRoomStatus(Long broadcastId, Long requestMemberId, boolean canChat) {
        Broadcast broadcast = broadcastDao.get(broadcastId);
        validAuthorization(broadcast, requestMemberId);
        broadcast.changeMessageRoomStatus(canChat);
        fflService.sendChangeMessageRoomStatusMessage(broadcast.getVideoKey(), canChat);
        return new CanChatResponse(broadcast.getId(), broadcast.getCanChat());
    }


    @Transactional
    public BroadcastPinChatResponse pinMessage(Long broadcastId, BroadcastPinMessageRequest request) {
        Broadcast broadcast = broadcastDao.get(broadcastId);
        if (request.isPin()) {
            return pin(request, broadcast);
        }
        return noPin(broadcast);
    }

    private BroadcastPinChatResponse pin(BroadcastPinMessageRequest request, Broadcast broadcast) {
        BroadcastPinMessage pinMessage = pinChatDao.find(broadcast)
                .map(message -> converter.merge(message, request))
                .orElse(converter.toEntity(broadcast, request));
        pinChatDao.save(pinMessage);
        PinMessageInfo pinMessageInfo = PinMessageInfo.pin(pinMessage);
        fflService.sendPinMessage(broadcast.getVideoKey(), pinMessageInfo);
        return BroadcastPinChatResponse.pin(pinMessageInfo);
    }

    private BroadcastPinChatResponse noPin(Broadcast broadcast) {
        pinChatDao.delete(broadcast);
        fflService.sendNoPinMessage(broadcast.getVideoKey());
        return BroadcastPinChatResponse.noPin();
    }

    private void validAuthorization(Broadcast broadcast, Long requestMemberId) {
        if (!broadcast.isCreatedByEq(requestMemberId)
                && !permissionDao.isManagerOrAdmin(broadcast.getId(), requestMemberId)) {
            throw new BadRequestException(ACCESS_DENIED, "not a admin or owner or manager of this broadcast");
        }
    }
}
