package com.jocoos.mybeautip.domain.vod.persistence.domain;

import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastCategory;
import lombok.Builder;

import java.time.ZonedDateTime;

@Builder
public record VodIntegrationInfo(String url,
                                 String title,
                                 String notice,
                                 String thumbnail,
                                 BroadcastCategory category,
                                 ZonedDateTime startedAt,
                                 Integer heartCount) {

    public static VodIntegrationInfo from(Broadcast broadcast) {
        return VodIntegrationInfo.builder()
                .title(broadcast.getTitle())
                .notice(broadcast.getNotice())
                .thumbnail(broadcast.getThumbnail())
                .category(broadcast.getCategory())
                .startedAt(broadcast.getStartedAt())
                .heartCount(broadcast.getHeartCount())
                .build();
    }
}
