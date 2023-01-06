package com.mybeautip.domain.notice.persistence.domain

import com.jocoos.mybeautip.global.config.jpa.PrimaryKeyEntity
import com.mybeautip.global.code.FileType
import javax.persistence.Column
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

//@Entity
class NoticeFile (
    @Column(nullable = false) var file: String,
    @Column(nullable = false) var type: FileType = FileType.IMAGE
) : PrimaryKeyEntity() {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "notice_id", nullable = false)
    var notice: Notice? = null

}