package com.jocoos.mybeautip.domain.notice.dto;

import com.jocoos.mybeautip.domain.popupnotice.code.NoticeStatus;
import com.jocoos.mybeautip.global.dto.SearchDto;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.ZonedDateTime;

@Getter
@Setter
@SuperBuilder
public class SearchNoticeRequest extends SearchDto {

    private NoticeStatus status;
    private String search;
    private ZonedDateTime startAt;
    private ZonedDateTime endAt;
    private Long cursor;
}
