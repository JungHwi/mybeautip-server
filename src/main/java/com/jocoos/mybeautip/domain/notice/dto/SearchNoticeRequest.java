package com.jocoos.mybeautip.domain.notice.dto;

import com.jocoos.mybeautip.domain.notice.code.NoticeStatus;
import com.jocoos.mybeautip.global.dto.SearchDto;
import com.jocoos.mybeautip.global.vo.SearchOption;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.ZonedDateTime;

@Getter
@Setter
@SuperBuilder
public class SearchNoticeRequest extends SearchDto {

    private NoticeStatus status;
    private Boolean isVisible;
    private Boolean isImportant;
    private SearchOption search;
    private Long cursor;

    public ZonedDateTime getStartAt() {
        return search == null ? null : search.getStartAt();
    }

    public ZonedDateTime getEndAt() {
        return search == null ? null : search.getEndAt();
    }

    public String getSearchKeyword() {
        return search == null ? null : search.getKeyword();
    }
}
