package com.jocoos.mybeautip.domain.broadcast.dto;

import lombok.Builder;

@Builder
public record BroadcastKey(String streamKey,
                           String gossipToken,
                           String channelKey,
                           String appId) { }
