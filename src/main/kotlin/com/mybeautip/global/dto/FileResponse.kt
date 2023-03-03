package com.mybeautip.global.dto

import com.mybeautip.global.code.FileType

data class FileResponse(val type: FileType, val file: String) {
}