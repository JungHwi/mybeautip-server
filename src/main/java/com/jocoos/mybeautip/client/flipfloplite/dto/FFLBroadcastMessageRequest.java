package com.jocoos.mybeautip.client.flipfloplite.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jocoos.mybeautip.client.flipfloplite.code.FFLChatRoomBroadcastMessageCustomType;
import com.jocoos.mybeautip.client.flipfloplite.code.FFLChatRoomBroadcastMessageType;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus;
import com.jocoos.mybeautip.domain.broadcast.dto.VisibleMessageRequest;
import com.jocoos.mybeautip.global.util.ObjectMapperUtil;
import lombok.Builder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jocoos.mybeautip.client.flipfloplite.code.FFLChatRoomBroadcastMessageCustomType.*;
import static com.jocoos.mybeautip.client.flipfloplite.code.FFLChatRoomBroadcastMessageType.COMMAND;

@Builder
public record FFLBroadcastMessageRequest(FFLChatRoomBroadcastMessageType messageType,
                                         FFLChatRoomBroadcastMessageCustomType customType,
                                         String message,
                                         Map<String, Object> data,
                                         List<String> appUserIds) {

    public static FFLBroadcastMessageRequest ofVisibleMessage(VisibleMessageRequest request) {
        FFLChatRoomBroadcastMessageCustomType type = request.isVisible() ? VISIBLE_MESSAGE : INVISIBLE_MESSAGE;
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("message_id", request.messageId());
        return FFLBroadcastMessageRequest.builder()
                .messageType(COMMAND)
                .customType(type)
                .data(dataMap)
                .build();
    }

    public static FFLBroadcastMessageRequest ofChangeChatStatus(boolean canChat) {
        return FFLBroadcastMessageRequest.builder()
                .messageType(COMMAND)
                .customType(canChat ? CHAT : NO_CHAT)
                .message(canChat ? CHAT.getSendMessage() : NO_CHAT.getSendMessage())
                .build();
    }

    public static FFLBroadcastMessageRequest ofBroadcastEdited(EditBroadcastChatData editBroadcastChatData,
                                                               ObjectMapper objectMapper) {
        return FFLBroadcastMessageRequest.builder()
                .messageType(COMMAND)
                .customType(UPDATE_BROADCAST)
                .data(ObjectMapperUtil.converToMap(editBroadcastChatData, objectMapper))
                .build();
    }

    public static FFLBroadcastMessageRequest ofChangeBroadcastStatus(BroadcastStatus status) {
        return FFLBroadcastMessageRequest.builder()
                .messageType(COMMAND)
                .customType(UPDATE_STATUS)
                .data(Map.of("status", status))
                .build();
    }


    public static FFLBroadcastMessageRequest ofChangeBroadcastStatus(BroadcastStatus status, Map<String, Object> data) {
        data.put("status", status);

        return FFLBroadcastMessageRequest.builder()
                .messageType(COMMAND)
                .customType(UPDATE_STATUS)
                .data(data)
                .build();
    }

    public static FFLBroadcastMessageRequest ofPin(PinMessageInfo pinChat,
                                                   ObjectMapper objectMapper) {
        return FFLBroadcastMessageRequest.builder()
                .messageType(COMMAND)
                .customType(PIN)
                .data(ObjectMapperUtil.converToMap(pinChat, objectMapper))
                .build();
    }

    public static FFLBroadcastMessageRequest ofNoPin() {
        return FFLBroadcastMessageRequest.builder()
                .messageType(COMMAND)
                .customType(NO_PIN)
                .build();
    }
}
