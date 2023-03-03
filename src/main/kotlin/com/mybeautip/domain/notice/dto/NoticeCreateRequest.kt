package com.mybeautip.domain.notice.dto

import com.jocoos.mybeautip.domain.popupnotice.code.PopupNoticeStatus
import com.jocoos.mybeautip.global.dto.FileDto

data class NoticeCreateRequest(val title: String,
                               val description: String,
                               val status: PopupNoticeStatus = PopupNoticeStatus.ACTIVE,
                               val files: List<FileDto> = arrayListOf()

)