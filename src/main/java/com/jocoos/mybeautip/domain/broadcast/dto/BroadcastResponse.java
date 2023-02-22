package com.jocoos.mybeautip.domain.broadcast.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus;
import com.jocoos.mybeautip.domain.member.dto.SimpleMemberInfo;
import com.jocoos.mybeautip.global.wrapper.CursorInterface;

import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;

public record BroadcastResponse(long id,
                                BroadcastStatus status,
                                BroadcastCategoryInfo category,
                                String title,
                                String thumbnailUrl,
                                int viewerCount,
                                SimpleMemberInfo member,
                                @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT) ZonedDateTime startedAt)
        implements CursorInterface {
    @JsonIgnore
    @Override
    public String getCursor() {
        return String.valueOf(id);
    }
}
