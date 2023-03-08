package com.jocoos.mybeautip.client.flipfloplite.dto;

import com.jocoos.mybeautip.client.flipfloplite.code.FFLChatRoomBroadcastMessageCustomType;
import com.jocoos.mybeautip.client.flipfloplite.code.FFLChatRoomBroadcastMessageType;

import java.util.List;

public record FFLBroadcastMessageRequest(FFLChatRoomBroadcastMessageType messageType,
                                         FFLChatRoomBroadcastMessageCustomType customType,
                                         String message,
                                         List<String> appUserIds) {
}
