package com.jocoos.mybeautip.domain.broadcast.dto;

import com.jocoos.mybeautip.client.flipfloplite.dto.ChatTokenAndAppId;
import lombok.Builder;

@Builder
public record BroadcastKey(String streamKey,
                           String gossipToken,
                           String channelKey,
                           String appId) {

    public static BroadcastKey withoutStreamKey(String channelKey, ChatTokenAndAppId chatTokenAndAppId) {
        return BroadcastKey.builder()
                .channelKey(channelKey)
                .gossipToken(chatTokenAndAppId.chatToken())
                .appId(chatTokenAndAppId.appId())
                .build();
    }

    public static BroadcastKey withStreamKey(String channelKey, ChatTokenAndAppId chatTokenAndAppId, String streamKey) {
        return BroadcastKey.builder()
                .channelKey(channelKey)
                .gossipToken(chatTokenAndAppId.chatToken())
                .appId(chatTokenAndAppId.appId())
                .streamKey(streamKey)
                .build();
    }
}
