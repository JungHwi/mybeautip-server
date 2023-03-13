package com.jocoos.mybeautip.client.flipfloplite.dto;

import com.jocoos.mybeautip.client.flipfloplite.code.FFLChatRoomDirectMessageCustomType;
import com.jocoos.mybeautip.client.flipfloplite.code.FFLChatRoomDirectMessageType;
import com.jocoos.mybeautip.domain.broadcast.dto.GrantManagerRequest;
import com.jocoos.mybeautip.domain.broadcast.dto.ViewerSuspendRequest;
import com.jocoos.mybeautip.global.util.StringConvertUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jocoos.mybeautip.client.flipfloplite.code.FFLChatRoomDirectMessageCustomType.*;

public record FFLDirectMessageRequest(FFLChatRoomDirectMessageType messageType,
                                      FFLChatRoomDirectMessageCustomType customType,
                                      String message,
                                      List<String> appUserIds) {

    public static FFLDirectMessageRequest of(GrantManagerRequest request, List<Long> memberIds) {
        Map<String, Object> contents = new HashMap<>();
        contents.put("managerId", request.memberId());

        if (request.isManager()) {
            return new FFLDirectMessageRequest(MANAGER, memberIds, StringConvertUtil.convertToJson(contents));
        } else {
            return new FFLDirectMessageRequest(NO_MANAGER, memberIds, StringConvertUtil.convertToJson(contents));
        }
    }

    public static FFLDirectMessageRequest of(ViewerSuspendRequest request) {
        if (request.isSuspended()) {
            return new FFLDirectMessageRequest(NO_CHAT, List.of(request.memberId()), null);
        } else {
            return new FFLDirectMessageRequest(CHAT, List.of(request.memberId()), null);
        }
    }

    public static FFLDirectMessageRequest ofExile(long memberId) {
        return new FFLDirectMessageRequest(EXILE, List.of(memberId), null);
    }

    private FFLDirectMessageRequest(FFLChatRoomDirectMessageCustomType customType, List<Long> memberIds, String message) {
        this(FFLChatRoomDirectMessageType.COMMAND, customType, message, memberIds.stream().map(String::valueOf).toList());
    }
}
