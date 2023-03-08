package com.jocoos.mybeautip.client.flipfloplite.dto;

import com.jocoos.mybeautip.client.flipfloplite.code.FFLChatRoomDirectMessageCustomType;
import com.jocoos.mybeautip.client.flipfloplite.code.FFLChatRoomDirectMessageType;

import java.util.List;

public record FFLDirectMessageRequest(FFLChatRoomDirectMessageType messageType,
                                      FFLChatRoomDirectMessageCustomType customType,
                                      String message,
                                      List<String> appUserIds) {
}
