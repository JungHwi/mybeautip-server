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
                                      List<String> appUserIds,
                                      String message,
                                      String data) {

    public static FFLDirectMessageRequest of(GrantManagerRequest request, List<Long> memberIds) {
        if (request.isManager()) {
            return new FFLDirectMessageRequest(MANAGER, memberIds, null, StringConvertUtil.convertToJson(contents));
        } else {
            return new FFLDirectMessageRequest(NO_MANAGER, memberIds, null, StringConvertUtil.convertToJson(contents));
        }
    }

    public static FFLDirectMessageRequest of(ViewerSuspendRequest request) {
        if (request.isSuspended()) {
            return new FFLDirectMessageRequest(NO_VIEWER_CHAT, List.of(request.memberId()), null, null);
        } else {
            return new FFLDirectMessageRequest(VIEWER_CHAT, List.of(request.memberId()), null, null);
        }
    }

    public static FFLDirectMessageRequest ofExile(Long memberId, String username) {
        return new FFLDirectMessageRequest(EXILE, List.of(memberId), String.format("[%s]님을 추방했습니다.", username), null);
    }

    public static FFLDirectMessageRequest ofManagerOut(long memberId) {
        return new FFLDirectMessageRequest(MANAGER_OUT, List.of(memberId), null, null);
    }

    private FFLDirectMessageRequest(FFLChatRoomDirectMessageCustomType customType, List<Long> memberIds, String message, String data) {
        this(FFLChatRoomDirectMessageType.COMMAND, customType, memberIds.stream().map(String::valueOf).toList(), message, data);
    }
}
