package com.jocoos.mybeautip.testutil.fixture

import com.jocoos.mybeautip.domain.operation.code.OperationTargetType
import com.jocoos.mybeautip.domain.operation.code.OperationTargetType.MEMBER
import com.jocoos.mybeautip.domain.operation.code.OperationType
import com.jocoos.mybeautip.domain.operation.code.OperationType.MEMBER_EXILE
import com.jocoos.mybeautip.domain.operation.persistence.domain.OperationLog
import com.jocoos.mybeautip.member.Member
import javax.persistence.*

fun makeOperationLog(
    targetId: String,
    createdBy: Member,
    id: Long? = null,
    targetType: OperationTargetType = MEMBER,
    operationType: OperationType = MEMBER_EXILE,
    description: String = "description",
) : OperationLog {
    return OperationLog(id, targetType, operationType, targetId, description, createdBy)
}
