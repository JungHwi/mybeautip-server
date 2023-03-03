package com.mybeautip.global.code

import com.jocoos.mybeautip.global.code.CodeValue

enum class FileType(val desc: String) : CodeValue {

    IMAGE("이미지 파일"),
    VIDEO("동영상 파일");

    override fun getDescription(): String = this.desc

    override fun getName(): String {
        return this.name
    }
}