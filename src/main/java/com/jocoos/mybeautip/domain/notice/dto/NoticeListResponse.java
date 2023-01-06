package com.jocoos.mybeautip.domain.notice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jocoos.mybeautip.domain.member.dto.SimpleMemberInfo;
import com.jocoos.mybeautip.domain.notice.code.NoticeStatus;
import com.jocoos.mybeautip.global.dto.FileDto;
import com.jocoos.mybeautip.global.wrapper.CursorInterface;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;

@Getter
@Setter
public class NoticeListResponse implements CursorInterface {

    private long id;

    private NoticeStatus status;

    private Boolean isVisible;

    private Boolean isImportant;

    private String title;

    private int viewCount;

    private SimpleMemberInfo createdBy;

    @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT)
    private ZonedDateTime createdAt;

    private FileDto file;

    @Override
    @JsonIgnore
    public String getCursor() {
        return String.valueOf(id);
    }
}
