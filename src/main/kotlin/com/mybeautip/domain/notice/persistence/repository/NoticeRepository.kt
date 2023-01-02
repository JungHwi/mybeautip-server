package com.mybeautip.domain.notice.persistence.repository

import com.jocoos.mybeautip.global.config.jpa.DefaultJpaRepository
import com.mybeautip.domain.notice.persistence.domain.Notice
import java.util.*

//@Repository
interface NoticeRepository : DefaultJpaRepository<Notice, UUID>{
}