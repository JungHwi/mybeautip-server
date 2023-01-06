package com.mybeautip.domain.notice.dto

import com.mybeautip.global.dto.FileResponse
import java.util.*

data class NoticeResponse(val id: UUID,
                          val title: String,
                          val description: String,
                          val files: List<FileResponse>?) {
}