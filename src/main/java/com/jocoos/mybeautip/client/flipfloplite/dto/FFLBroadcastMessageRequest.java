package com.jocoos.mybeautip.client.flipfloplite.dto;

import com.jocoos.mybeautip.client.flipfloplite.code.FFLChatRoomBroadcastMessageCustomType;
import com.jocoos.mybeautip.client.flipfloplite.code.FFLChatRoomBroadcastMessageType;
import com.jocoos.mybeautip.domain.broadcast.dto.VisibleMessageRequest;
import com.jocoos.mybeautip.global.util.StringConvertUtil;

import java.util.HashMap;
import java.util.Map;

import static com.jocoos.mybeautip.client.flipfloplite.code.FFLChatRoomBroadcastMessageCustomType.INVISIBLE_MESSAGE;
import static com.jocoos.mybeautip.client.flipfloplite.code.FFLChatRoomBroadcastMessageCustomType.VISIBLE_MESSAGE;

public record FFLBroadcastMessageRequest(FFLChatRoomBroadcastMessageType messageType,
                                         FFLChatRoomBroadcastMessageCustomType customType,
                                         String message,
                                         String data) {

    public static FFLBroadcastMessageRequest ofVisibleMessage(VisibleMessageRequest request) {
        FFLChatRoomBroadcastMessageCustomType type = request.isVisible() ? VISIBLE_MESSAGE : INVISIBLE_MESSAGE;
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("messageId", request.messageId());
        return new FFLBroadcastMessageRequest(type, null, StringConvertUtil.convertToJson(dataMap));
    }

    private FFLBroadcastMessageRequest(FFLChatRoomBroadcastMessageCustomType customType, String message, String data) {
        this(FFLChatRoomBroadcastMessageType.COMMAND, customType, message, data);
    }
}
