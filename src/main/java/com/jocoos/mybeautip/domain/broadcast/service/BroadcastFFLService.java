package com.jocoos.mybeautip.domain.broadcast.service;

import com.jocoos.mybeautip.client.flipfloplite.FlipFlopLiteService;
import com.jocoos.mybeautip.client.flipfloplite.converter.FlipFlopLiteConverter;
import com.jocoos.mybeautip.client.flipfloplite.dto.*;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastKey;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus.CANCEL;
import static com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus.END;

@RequiredArgsConstructor
@Service
public class BroadcastFFLService {

    private final FlipFlopLiteService flipFlopLiteService;
    private final FlipFlopLiteConverter converter;

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
        sendChangeBroadcastStatusMessage(broadcast.getVideoKey(), BroadcastStatus.LIVE);
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

    public void sendChangeChatStatusMessage(Long videoRoomId, boolean canChat) {
        FFLBroadcastMessageRequest request = FFLBroadcastMessageRequest.changeChatStatus(canChat);
        flipFlopLiteService.broadcastMessage(videoRoomId, request);
    }

    public void sendBroadcastEditedMessage(Broadcast broadcast) {
        ChatData chatData = ChatData.editBroadcast(broadcast);
        FFLBroadcastMessageRequest request = FFLBroadcastMessageRequest.broadcastEdited(chatData.toJson());
        flipFlopLiteService.broadcastMessage(broadcast.getVideoKey(), request);
    }

    public void sendChangeBroadcastStatusMessage(Long videoRoomId, BroadcastStatus status) {
        ChatData chatData = ChatData.changeStatus(status);
        FFLBroadcastMessageRequest request = FFLBroadcastMessageRequest.changeBroadcastStatus(chatData.toJson());
        flipFlopLiteService.broadcastMessage(videoRoomId, request);
    }
}
