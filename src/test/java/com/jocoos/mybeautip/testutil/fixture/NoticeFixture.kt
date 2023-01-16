package com.jocoos.mybeautip.testutil.fixture

import com.jocoos.mybeautip.domain.notice.code.NoticeStatus
import com.jocoos.mybeautip.domain.notice.code.NoticeStatus.NORMAL
import com.jocoos.mybeautip.domain.notice.persistence.domain.Notice
import com.jocoos.mybeautip.domain.notice.persistence.domain.NoticeFile
import com.jocoos.mybeautip.member.Member

fun makeNotice(
    id: Long? = null,
    status: NoticeStatus = NORMAL,
    isVisible: Boolean = true,
    isImportant: Boolean = false,
    title: String = "title",
    description: String = "description",
    viewCount: Int = 0,
    files: List<NoticeFile> = mutableListOf()
): Notice {
    return Notice(id, status, isVisible, isImportant, title, description, viewCount, files)
}

