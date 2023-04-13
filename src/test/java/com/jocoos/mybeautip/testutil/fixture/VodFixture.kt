package com.jocoos.mybeautip.testutil.fixture

import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast
import com.jocoos.mybeautip.domain.vod.code.VodStatus
import com.jocoos.mybeautip.domain.vod.code.VodStatus.AVAILABLE
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastCategory
import com.jocoos.mybeautip.domain.scrap.code.ScrapType
import com.jocoos.mybeautip.domain.scrap.persistence.domain.Scrap
import com.jocoos.mybeautip.domain.vod.code.VodStatus.CREATED
import com.jocoos.mybeautip.domain.vod.persistence.domain.Vod
import com.jocoos.mybeautip.domain.vod.persistence.domain.VodIntegrationInfo
import java.time.ZonedDateTime

fun makeVod(
    category: BroadcastCategory,
    memberId: Long,
    videoKey: Long = 1,
    broadcastId: Long = 1,
    chatChannelKey: String = "chatKey",
    status: VodStatus = AVAILABLE,
    visibility: Boolean = true,
    title: String = "title",
    thumbnail: String = "thumbnail"
) : Vod {
    val vod = Vod(
        videoKey,
        broadcastId,
        chatChannelKey,
        title,
        thumbnail,
        category,
        status,
        visibility,
        memberId
    )
    vod.integrate(makeVodIntegrationInfo(title = title, category = category))
    return vod
}

fun makeVod(
    broadcast: Broadcast
) : Vod {
    val vod = Vod(
        broadcast.videoKey,
        broadcast.id,
        broadcast.chatChannelKey,
        broadcast.title,
        broadcast.thumbnail,
        broadcast.category,
        CREATED,
        true,
        broadcast.memberId)
    vod.integrate(makeVodIntegrationInfo(category = broadcast.category))
    return vod
}

fun makeVodIntegrationInfo(
    url: String = "url",
    title: String = "integration title",
    notice: String = "integration notice",
    thumbnail: String = "thumbnail",
    category: BroadcastCategory,
    startedAt: ZonedDateTime = ZonedDateTime.now().minusDays(1),
    transcodeAt: ZonedDateTime = ZonedDateTime.now(),
    heartCount: Int = 0
) : VodIntegrationInfo {
    return VodIntegrationInfo(url, title, notice, thumbnail, category, startedAt, heartCount, transcodeAt)
}

fun makeVodScrap(
    memberId: Long,
    vodId: Long,
) : Scrap {
    val scrap = Scrap(memberId, ScrapType.VOD, vodId)
    scrap.apply { isScrap = true }
    return scrap
}
