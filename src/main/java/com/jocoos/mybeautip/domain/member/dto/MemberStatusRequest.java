package com.jocoos.mybeautip.domain.member.dto;

import com.jocoos.mybeautip.domain.member.code.MemberStatus;
import com.jocoos.mybeautip.domain.operation.code.OperationType;
import com.jocoos.mybeautip.domain.operation.service.dao.OperationLogInterface;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.member.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
@NoArgsConstructor
public class MemberStatusRequest implements OperationLogInterface {

    private Long memberId;
    private MemberStatus beforeStatus;
    private MemberStatus afterStatus;
    private String description;
    private Long adminId;
    private Member adminMember;

    public MemberStatusRequest(MemberStatus status) {
        this.afterStatus = status;
    }

    @Override
    public OperationType getOperationType() {
        switch (afterStatus) {
            case SUSPENDED -> {
                return OperationType.MEMBER_SUSPENDED;
            }
            case EXILE -> {
                return OperationType.MEMBER_EXILE;
            }
            default -> throw new BadRequestException("Can not write operation log.");
        }
    }

    @Override
    public String getTargetId() {
        return String.valueOf(this.memberId);
    }

    @Override
    public String getDescription() {
        return String.format("%s -> %s. %s", beforeStatus, afterStatus, StringUtils.isEmpty(description) ? "" : description);
    }

    @Override
    public Member getCreatedBy() {
        return adminMember;
    }
}
