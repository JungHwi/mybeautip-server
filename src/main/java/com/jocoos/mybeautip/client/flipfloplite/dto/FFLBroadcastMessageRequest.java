package com.jocoos.mybeautip.client.flipfloplite.dto;

import com.jocoos.mybeautip.client.flipfloplite.code.FFLChatRoomBroadcastMessageCustomType;
import com.jocoos.mybeautip.client.flipfloplite.code.FFLChatRoomBroadcastMessageType;
import com.jocoos.mybeautip.domain.broadcast.dto.VisibleMessageRequest;
import com.jocoos.mybeautip.global.util.StringConvertUtil;
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
                                         String data,
                                         List<String> appUserIds) {

    public static FFLBroadcastMessageRequest ofVisibleMessage(VisibleMessageRequest request) {
        FFLChatRoomBroadcastMessageCustomType type = request.isVisible() ? VISIBLE_MESSAGE : INVISIBLE_MESSAGE;
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("messageId", request.messageId());
        return FFLBroadcastMessageRequest.builder()
                .messageType(COMMAND)
                .customType(type)
                .data(StringConvertUtil.convertToJson(dataMap))
                .build();
    }

    public static FFLBroadcastMessageRequest ofChangeChatStatus(boolean canChat) {
        return FFLBroadcastMessageRequest.builder()
                .messageType(COMMAND)
                .customType(canChat ? CHAT : NO_CHAT)
                .message(canChat ? CHAT.getSendMessage() : NO_CHAT.getSendMessage())
                .build();
    }

    public static FFLBroadcastMessageRequest ofBroadcastEdited(String editedBroadcastInfoJson) {
        return FFLBroadcastMessageRequest.builder()
                .messageType(COMMAND)
                .customType(UPDATE_BROADCAST)
                .data(editedBroadcastInfoJson)
                .build();
    }

    public static FFLBroadcastMessageRequest ofChangeBroadcastStatus(String statusChangeJson) {
        return FFLBroadcastMessageRequest.builder()
                .messageType(COMMAND)
                .customType(UPDATE_STATUS)
                .data(statusChangeJson)
                .build();
    }
}
