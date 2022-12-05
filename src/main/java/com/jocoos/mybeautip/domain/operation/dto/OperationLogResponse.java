package com.jocoos.mybeautip.domain.operation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jocoos.mybeautip.domain.member.dto.SimpleMemberInfo;
import com.jocoos.mybeautip.domain.operation.code.OperationType;
import com.jocoos.mybeautip.global.wrapper.CursorInterface;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;

@Getter
@Builder
@AllArgsConstructor
public class OperationLogResponse implements CursorInterface {

    private long id;

    private OperationType operationType;

    @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT)
    private ZonedDateTime createdAt;

    private SimpleMemberInfo adminMember;

    @Override
    public String getCursor() {
        return String.valueOf(this.id);
    }
}
