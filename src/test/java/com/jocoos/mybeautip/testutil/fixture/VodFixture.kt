package com.jocoos.mybeautip.testutil.fixture

import com.jocoos.mybeautip.domain.broadcast.code.VodStatus
import com.jocoos.mybeautip.domain.broadcast.code.VodStatus.AVAILABLE
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastCategory
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Vod

fun makeVod(
    category: BroadcastCategory,
    memberId: Long,
    videoKey: Long = 1,
    visibility: Boolean = true,
    status: VodStatus = AVAILABLE,
    title: String = "title",
    thumbnail: String = "thumbnail"
) : Vod {
    val vod = Vod(videoKey, title, thumbnail, category, memberId)
    vod.changeVisibility(visibility)
    vod.changeStatus(status)
    return vod
}
