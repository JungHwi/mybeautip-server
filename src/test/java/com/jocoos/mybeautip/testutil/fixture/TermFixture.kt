package com.jocoos.mybeautip.testutil.fixture

import com.jocoos.mybeautip.domain.term.code.TermStatus
import com.jocoos.mybeautip.domain.term.code.TermStatus.OPTIONAL
import com.jocoos.mybeautip.domain.term.code.TermType
import com.jocoos.mybeautip.domain.term.code.TermType.MARKETING_INFO
import com.jocoos.mybeautip.domain.term.code.TermUsedInType
import com.jocoos.mybeautip.domain.term.persistence.domain.Term

fun makeTerm(
    id: Long? = null,
    type: TermType = MARKETING_INFO,
    title: String = "title",
    content: String = "content",
    currentTermStatus: TermStatus = OPTIONAL,
    usedInType: List<TermUsedInType> = emptyList(),
    version: Float = 1.0f,
    versionChangeStatus: TermStatus = OPTIONAL
) : Term {
    return Term(id, type, title, content, currentTermStatus, usedInType, version, versionChangeStatus)
}
