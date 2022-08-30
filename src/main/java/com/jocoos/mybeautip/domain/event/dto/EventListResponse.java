package com.jocoos.mybeautip.domain.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jocoos.mybeautip.domain.event.code.EventStatus;
import com.jocoos.mybeautip.domain.event.code.EventType;
import com.jocoos.mybeautip.global.wrapper.CursorInterface;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;

@Getter
@Setter
public class EventListResponse implements CursorInterface {
    private long id;
    private EventType type;
    private EventStatus status;
    private String title;
    private String thumbnailImageUrl;
    private String bannerImageUrl;

    @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT)
    private ZonedDateTime startAt;

    @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT)
    private ZonedDateTime endAt;

    @Override
    @JsonIgnore
    public String getCursor() {
        return String.valueOf(this.id);
    }
}
