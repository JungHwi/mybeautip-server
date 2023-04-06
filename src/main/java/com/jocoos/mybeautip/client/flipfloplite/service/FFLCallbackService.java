package com.jocoos.mybeautip.client.flipfloplite.service;

import com.jocoos.mybeautip.client.flipfloplite.FlipFlopLiteService;
import com.jocoos.mybeautip.client.flipfloplite.code.FFLCallbackType;
import com.jocoos.mybeautip.client.flipfloplite.code.FFLCallbackType.FFLCallbackRequestType;
import com.jocoos.mybeautip.client.flipfloplite.code.FFLStreamKeyState;
import com.jocoos.mybeautip.client.flipfloplite.code.FFLVideoRoomState;
import com.jocoos.mybeautip.client.flipfloplite.dto.ExternalBroadcastInfo;
import com.jocoos.mybeautip.client.flipfloplite.dto.FFLCallbackData;
import com.jocoos.mybeautip.client.flipfloplite.dto.FFLDirectMessageRequest;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastViewer;
import com.jocoos.mybeautip.domain.broadcast.service.child.BroadcastDomainService;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastDao;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastViewerDao;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.global.util.EntityUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.time.ZonedDateTime;
import java.util.List;

import static com.jocoos.mybeautip.client.flipfloplite.code.FFLStreamKeyState.ACTIVE;
import static com.jocoos.mybeautip.client.flipfloplite.code.FFLVideoRoomState.LIVE;
import static com.jocoos.mybeautip.client.flipfloplite.code.FFLVideoRoomState.LIVE_INACTIVE;
import static com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus.READY;

@Slf4j
@RequiredArgsConstructor
@Service
public class FFLCallbackService {

    private final BroadcastDomainService broadcastDomainService;
    private final BroadcastDao broadcastDao;
    private final FlipFlopLiteService flipFlopLiteService;
    private final BroadcastViewerDao viewerDao;

    @Transactional
    public void callback(String requestId, FFLCallbackType type, FFLCallbackData data) {
        switch (FFLCallbackRequestType.getRequestType(type)) {
            case VIDEO_ROOM_STATUS_CHANGE -> updateBroadcast(requestId, type, data);
            case STREAM_KEY_STATUS_CHANGE -> sendStreamKeyStateChangedMessage(data);
        }
    }

    @SneakyThrows
    private void updateBroadcast(String requestId, FFLCallbackType type, FFLCallbackData data) {
        log.info("Request Id : {} Callback Type : {}", requestId, type);
        FFLVideoRoomState fflVideoRoomState = getVideoRoomState(type, data);
        log.info("Request Id : {} FFLVideoRoomState : {}", requestId, fflVideoRoomState);

        Long videoRoomId = data.videoRoomId();
        if (LIVE.equals(fflVideoRoomState) || LIVE_INACTIVE.equals(fflVideoRoomState)) {
            broadcastDomainService.updatePausedAt(videoRoomId, getPausedAt(fflVideoRoomState));
        }
        if (LIVE.equals(fflVideoRoomState)) {
            ExternalBroadcastInfo info = flipFlopLiteService.getVideoRoom(videoRoomId);
            log.info("Request Id : {}  Video Key : {} Video Room Info : {}", requestId, videoRoomId, info);
            String liveUrl = info.liveUrl();
            if (liveUrl == null) {
                liveUrl = retry(videoRoomId);
            }
            broadcastDomainService.updateUrl(videoRoomId, liveUrl);
        }
    }

    @SneakyThrows
    private String retry(Long videoRoomId) {
        for (int i = 0; i < 5; i++) {
            log.info("Request For Get Live Video URL Is Null. Retry. Video Key {}", videoRoomId);
            Thread.sleep(1000);
            ExternalBroadcastInfo retryInfo = flipFlopLiteService.getVideoRoom(videoRoomId);
            String retryUrl = retryInfo.liveUrl();
            if (retryUrl != null) return retryUrl;
        }
        log.error("Request For Get Live Video URL Failed. Video Key {}", videoRoomId);
        throw new IllegalArgumentException("Live Callback URL Is Null");
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
        List<Long> broadcastIds = EntityUtil.extractLongList(activeOwners, owner -> owner.getBroadcast().getId());
        List<Broadcast> broadcasts = broadcastDao.getAllByCreatorIdIn(broadcastIds, List.of(READY));
        for (Broadcast broadcast : broadcasts) {
            flipFlopLiteService.directMessage(broadcast.getVideoKey(), request);
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
