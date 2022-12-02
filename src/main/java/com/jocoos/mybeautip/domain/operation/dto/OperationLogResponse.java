package com.jocoos.mybeautip.domain.operation.dto;

import com.jocoos.mybeautip.domain.operation.code.OperationType;
import com.jocoos.mybeautip.global.wrapper.CursorInterface;
import com.jocoos.mybeautip.member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
@Builder
@AllArgsConstructor
public class OperationLogResponse implements CursorInterface {

    private long id;

    private OperationType operationType;

    private ZonedDateTime createdAt;

    private Member adminMember;

    @Override
    public String getCursor() {
        return String.valueOf(this.id);
    }
}
