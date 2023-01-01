package com.jocoos.mybeautip.domain.notice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jocoos.mybeautip.domain.member.dto.SimpleMemberInfo;
import com.jocoos.mybeautip.domain.popupnotice.code.NoticeStatus;
import com.jocoos.mybeautip.global.dto.FileDto;
import com.jocoos.mybeautip.global.wrapper.CursorInterface;
import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.List;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;

@Getter
@Builder
public class NoticeResponse implements CursorInterface {

    private long id;

    private NoticeStatus status;

    private Boolean isImportant;

    private String title;

    private String description;

    private int viewCount;

    private SimpleMemberInfo modifiedBy;

    @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT)
    private ZonedDateTime modifiedAt;

    private SimpleMemberInfo createdBy;

    @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT)
    private ZonedDateTime createdAt;

    private List<FileDto> files;

    @Override
    public String getCursor() {
        return String.valueOf(id);
    }
}
