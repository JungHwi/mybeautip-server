package com.mybeautip.domain.notice.dto

import com.jocoos.mybeautip.domain.popupnotice.code.NoticeStatus
import com.jocoos.mybeautip.global.dto.FileDto

data class NoticeCreateRequest(val title: String,
                               val description: String,
                               val status: NoticeStatus = NoticeStatus.ACTIVE,
                               val files: List<FileDto> = arrayListOf()

)