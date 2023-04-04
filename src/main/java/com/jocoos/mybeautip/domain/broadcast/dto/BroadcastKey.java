package com.jocoos.mybeautip.domain.broadcast.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jocoos.mybeautip.client.flipfloplite.dto.ChatTokenAndAppId;
import lombok.Builder;

import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;

@Builder
public record BroadcastKey(String streamKey,
                           String gossipToken,
                           String channelKey,
                           String appId,
                           @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT) ZonedDateTime createdLiveAt) {

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

    public static BroadcastKey forVod(String channelKey, ChatTokenAndAppId chatTokenAndAppId, ZonedDateTime startedAt) {
        return BroadcastKey.builder()
                .channelKey(channelKey)
                .gossipToken(chatTokenAndAppId.chatToken())
                .appId(chatTokenAndAppId.appId())
                .createdLiveAt(startedAt)
                .build();
    }
}
