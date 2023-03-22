package com.jocoos.mybeautip.client.flipfloplite.service;

import com.jocoos.mybeautip.client.flipfloplite.FlipFlopLiteService;
import com.jocoos.mybeautip.client.flipfloplite.code.FFLCallbackType;
import com.jocoos.mybeautip.client.flipfloplite.code.FFLCallbackType.FFLCallbackRequestType;
import com.jocoos.mybeautip.client.flipfloplite.code.FFLStreamKeyState;
import com.jocoos.mybeautip.client.flipfloplite.code.FFLVideoRoomState;
import com.jocoos.mybeautip.client.flipfloplite.dto.FFLCallbackData;
import com.jocoos.mybeautip.client.flipfloplite.dto.FFLDirectMessageRequest;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastViewer;
import com.jocoos.mybeautip.domain.broadcast.service.child.BroadcastDomainService;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastViewerDao;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.time.ZonedDateTime;
import java.util.List;

import static com.jocoos.mybeautip.client.flipfloplite.code.FFLStreamKeyState.ACTIVE;

@Slf4j
@RequiredArgsConstructor
@Service
public class FFLCallbackService {

    private final BroadcastDomainService broadcastDomainService;
    private final FlipFlopLiteService flipFlopLiteService;
    private final BroadcastViewerDao viewerDao;

    @Transactional
    public void callback(FFLCallbackType type, FFLCallbackData data) {
        switch (FFLCallbackRequestType.getRequestType(type)) {
            case VIDEO_ROOM_STATUS_CHANGE -> updatePausedAt(type, data);
            case STREAM_KEY_STATUS_CHANGE -> sendStreamKeyStateChangedMessage(data);
        }
    }

    private void updatePausedAt(FFLCallbackType type, FFLCallbackData data) {
        FFLVideoRoomState fflVideoRoomState = getVideoRoomState(type, data);
        broadcastDomainService.updatePausedAt(data.videoRoomId(), getPausedAt(fflVideoRoomState));
    }

    private void sendStreamKeyStateChangedMessage(FFLCallbackData data) {
        FFLStreamKeyState currentStreamKeyState = data.streamKeyStreamKeyState();
        if (ACTIVE.equals(currentStreamKeyState)) {
            sendStreamKeyActiveDirectMessage(data, currentStreamKeyState);
        }
    }

    private void sendStreamKeyActiveDirectMessage(FFLCallbackData data, FFLStreamKeyState currentStreamKeyState) {
        Long memberId = flipFlopLiteService.getMemberIdFrom(data.streamKeyId());
        FFLDirectMessageRequest request =
                FFLDirectMessageRequest.ofStreamKeyStateChanged(memberId, currentStreamKeyState);
        List<BroadcastViewer> activeOwners = viewerDao.getActiveOwner(memberId);
        for (BroadcastViewer owner : activeOwners) {
            Broadcast ownerActiveBroadcast = owner.getBroadcast();
            log.debug("broadcast will be live : {}, {}", ownerActiveBroadcast.getId(), ownerActiveBroadcast.getStatus());
            flipFlopLiteService.directMessage(ownerActiveBroadcast.getId(), request);
        }
    }

    @Nullable
    private ZonedDateTime getPausedAt(FFLVideoRoomState videoRoomState) {
        return switch (videoRoomState) {
            case LIVE -> null;
            case LIVE_INACTIVE -> ZonedDateTime.now();
            default -> throw new BadRequestException("Invalid video room state");
        };
    }

    private FFLVideoRoomState getVideoRoomState(FFLCallbackType type, FFLCallbackData data) {
        FFLVideoRoomState fflVideoRoomState = data.videoRoomVideoRoomState();
        return fflVideoRoomState == null ? type.toVideoRoomState() : fflVideoRoomState;
    }
}
