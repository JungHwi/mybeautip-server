package com.jocoos.mybeautip.client.flipfloplite.dto;

import com.jocoos.mybeautip.client.flipfloplite.code.FFLChatRoomDirectMessageCustomType;
import com.jocoos.mybeautip.client.flipfloplite.code.FFLChatRoomDirectMessageType;
import com.jocoos.mybeautip.client.flipfloplite.code.FFLStreamKeyState;
import com.jocoos.mybeautip.domain.broadcast.dto.GrantManagerRequest;
import com.jocoos.mybeautip.domain.broadcast.dto.ViewerSuspendRequest;

import java.util.List;
import java.util.Map;

import static com.jocoos.mybeautip.client.flipfloplite.code.FFLChatRoomDirectMessageCustomType.*;

public record FFLDirectMessageRequest(FFLChatRoomDirectMessageType messageType,
                                      FFLChatRoomDirectMessageCustomType customType,
                                      List<String> appUserIds,
                                      String message,
                                      Map<String, Object> data) {

    public static FFLDirectMessageRequest of(GrantManagerRequest request, List<Long> memberIds) {
        if (request.isManager()) {
            return new FFLDirectMessageRequest(MANAGER, memberIds, null, null);
        } else {
            return new FFLDirectMessageRequest(NO_MANAGER, memberIds, null, null);
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
        return new FFLDirectMessageRequest(EXILE, List.of(memberId), null, null);
    }

    public static FFLDirectMessageRequest ofManagerOut(long memberId) {
        return new FFLDirectMessageRequest(MANAGER_OUT, List.of(memberId), null, null);
    }

    public static FFLDirectMessageRequest ofStreamKeyStateChanged(Long memberId, FFLStreamKeyState state) {
        Map<String, Object> data = Map.of("state", state);
        return new FFLDirectMessageRequest(STREAM_KEY_STATUS_CHANGED, List.of(memberId), null, data);
    }

    private FFLDirectMessageRequest(FFLChatRoomDirectMessageCustomType customType, List<Long> memberIds, String message, Map<String, Object> data) {
        this(FFLChatRoomDirectMessageType.COMMAND, customType, memberIds.stream().map(String::valueOf).toList(), message, data);
    }
}
