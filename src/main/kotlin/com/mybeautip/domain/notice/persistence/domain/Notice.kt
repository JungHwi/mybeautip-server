package com.mybeautip.domain.notice.persistence.domain

import com.jocoos.mybeautip.domain.popupnotice.code.NoticeStatus
import com.jocoos.mybeautip.global.config.jpa.PrimaryKeyEntity
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.FetchType
import javax.persistence.OneToMany

//@Entity
class Notice
    (title: String,
             description: String,
             status: NoticeStatus = NoticeStatus.ACTIVE,
             files: MutableList<NoticeFile>? = mutableListOf()): PrimaryKeyEntity() {

    @Column(nullable = false)
    var status: NoticeStatus = status
        protected set

    @Column(nullable = false)
    var title: String = title
        protected set

    @Column
    var description: String = description
        protected set

    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], mappedBy = "notice")
    protected var files: MutableList<NoticeFile>? = files
    val fileList: List<NoticeFile>? get() = files?.toList()

    fun addFile(file: NoticeFile) {
        this.files?.plusAssign(file)
    }
}