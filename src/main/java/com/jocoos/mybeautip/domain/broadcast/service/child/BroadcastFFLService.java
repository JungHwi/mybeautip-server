package com.jocoos.mybeautip.domain.broadcast.service.child;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jocoos.mybeautip.client.flipfloplite.FlipFlopLiteService;
import com.jocoos.mybeautip.client.flipfloplite.converter.FlipFlopLiteConverter;
import com.jocoos.mybeautip.client.flipfloplite.dto.*;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastKey;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastEditResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Map;

import static com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus.*;

@RequiredArgsConstructor
@Service
public class BroadcastFFLService {

    private final FlipFlopLiteService flipFlopLiteService;
    private final FlipFlopLiteConverter converter;
    private final ObjectMapper objectMapper;

    public BroadcastKey getBroadcastKey(Long requestMemberId, String channelKey) {
        ChatTokenAndAppId chatTokenAndAppId = flipFlopLiteService.getChatToken(requestMemberId);
        return BroadcastKey.withoutStreamKey(channelKey, chatTokenAndAppId);
    }

    public BroadcastKey getBroadcastKeyWithStreamKey(Long requestMemberId, String channelKey, Long streamKeyMemberId) {
        ChatTokenAndAppId chatTokenAndAppId = flipFlopLiteService.getChatToken(requestMemberId);
        String streamKey = flipFlopLiteService.getStreamKey(streamKeyMemberId);
        return BroadcastKey.withStreamKey(channelKey, chatTokenAndAppId, streamKey);
    }

    public BroadcastKey getGuestBroadcastKey(String guestName, String channelKey) {
        ChatTokenAndAppId chatTokenAndAppId = flipFlopLiteService.getGuestChatToken(guestName);
        return BroadcastKey.withoutStreamKey(channelKey, chatTokenAndAppId);
    }

    public ExternalBroadcastInfo createVideoRoom(Broadcast broadcast) {
        FFLVideoRoomRequest fflRequest = converter.converts(broadcast);
        return flipFlopLiteService.createVideoRoom(fflRequest);
    }

    public ExternalBroadcastInfo startFFLVideoRoomAndSendChatMessage(Broadcast broadcast) {
        ExternalBroadcastInfo externalInfo = flipFlopLiteService.startVideoRoom(broadcast.getVideoKey());
        Map<String, Object> data = Map.of(
                "url", externalInfo.liveUrl(),
                "started_at", externalInfo.lastModifiedAt());

        sendChangeBroadcastStatusMessage(broadcast.getVideoKey(), BroadcastStatus.LIVE, data);
        return externalInfo;
    }

    public ZonedDateTime endVideoRoomAndSendChatMessage(Long videoKey) {
        ZonedDateTime endedAt = flipFlopLiteService.endVideoRoom(videoKey);
        sendChangeBroadcastStatusMessage(videoKey, END);
        return endedAt;
    }

    public ZonedDateTime cancelVideoRoomAndSendChatMessage(Long videoKey) {
        ZonedDateTime canceledAt = flipFlopLiteService.cancelVideoRoom(videoKey);
        sendChangeBroadcastStatusMessage(videoKey, CANCEL);
        return canceledAt;
    }

    public void sendBroadcastEditedMessage(BroadcastEditResult editResult) {
        Broadcast broadcast = editResult.broadcast();
        EditBroadcastChatData editBroadcastChatData = EditBroadcastChatData.editBroadcast(broadcast);
        FFLBroadcastMessageRequest request = FFLBroadcastMessageRequest.ofBroadcastEdited(editBroadcastChatData, objectMapper);
        flipFlopLiteService.broadcastMessage(broadcast.getVideoKey(), request);
        if (editResult.isStatusChangedToReady()) {
            sendChangeBroadcastStatusMessage(broadcast.getVideoKey(), READY);
        }
    }

    public void sendChangeBroadcastStatusMessage(Long videoRoomId, BroadcastStatus status) {
        FFLBroadcastMessageRequest request = FFLBroadcastMessageRequest.ofChangeBroadcastStatus(status);
        flipFlopLiteService.broadcastMessage(videoRoomId, request);
    }
    public void sendChangeBroadcastStatusMessage(Long videoRoomId, BroadcastStatus status, Map<String, Object> data) {
        FFLBroadcastMessageRequest request = FFLBroadcastMessageRequest.ofChangeBroadcastStatus(status, data);
        flipFlopLiteService.broadcastMessage(videoRoomId, request);
    }

    public void sendChangeMessageRoomStatusMessage(Long videoRoomId, boolean canChat) {
        FFLBroadcastMessageRequest request = FFLBroadcastMessageRequest.ofChangeChatStatus(canChat);
        flipFlopLiteService.broadcastMessage(videoRoomId, request);
    }

    public void sendPinMessage(Long videoRoomId, PinMessageInfo pinChat) {
        FFLBroadcastMessageRequest request = FFLBroadcastMessageRequest.ofPin(pinChat, objectMapper);
        flipFlopLiteService.broadcastMessage(videoRoomId, request);
    }

    public void sendNoPinMessage(Long videoRoomId) {
        FFLBroadcastMessageRequest request = FFLBroadcastMessageRequest.ofNoPin();
        flipFlopLiteService.broadcastMessage(videoRoomId, request);
    }
}
