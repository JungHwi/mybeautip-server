package com.jocoos.mybeautip.testutil.fixture

import com.jocoos.mybeautip.domain.vod.code.VodStatus
import com.jocoos.mybeautip.domain.vod.code.VodStatus.AVAILABLE
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastCategory
import com.jocoos.mybeautip.domain.scrap.code.ScrapType
import com.jocoos.mybeautip.domain.scrap.persistence.domain.Scrap
import com.jocoos.mybeautip.domain.vod.persistence.domain.Vod

fun makeVod(
    category: BroadcastCategory,
    memberId: Long,
    videoKey: Long = 1,
    visibility: Boolean = true,
    status: VodStatus = AVAILABLE,
    title: String = "title",
    thumbnail: String = "thumbnail"
) : Vod {
    return Vod(
        videoKey,
        title,
        thumbnail,
        category,
        status,
        visibility,
        memberId
    )
}

fun makeVodScrap(
    memberId: Long,
    vodId: Long,
) : Scrap {
    val scrap = Scrap(memberId, ScrapType.VOD, vodId)
    scrap.apply { isScrap = true }
    return scrap
}
