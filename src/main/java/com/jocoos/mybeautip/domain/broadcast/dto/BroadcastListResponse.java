package com.jocoos.mybeautip.domain.broadcast.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus;
import com.jocoos.mybeautip.domain.member.dto.SimpleMemberInfo;
import com.jocoos.mybeautip.global.wrapper.CursorInterface;

import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;

public record BroadcastListResponse(long id,
                                    BroadcastStatus status,
                                    BroadcastCategoryResponse category,
                                    String url,
                                    String title,
                                    String thumbnailUrl,
                                    int viewerCount,
                                    int heartCount,
                                    @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT) ZonedDateTime startedAt,
                                    SimpleMemberInfo createdBy,
                                    BroadcastRelationInfo relationInfo)
        implements CursorInterface {
    @JsonIgnore
    @Override
    public String getCursor() {
        return String.valueOf(id);
    }
}
