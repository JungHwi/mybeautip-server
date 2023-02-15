package com.jocoos.mybeautip.testutil.fixture

import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastViewerStatus
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastViewerType
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastViewer
import com.jocoos.mybeautip.domain.broadcast.service.util.ViewerUsernameUtil
import com.jocoos.mybeautip.domain.member.code.InfluencerStatus
import com.jocoos.mybeautip.domain.member.persistence.domain.Influencer
import com.jocoos.mybeautip.member.Member
import org.apache.tika.utils.StringUtils
import java.time.ZonedDateTime

fun makeBroadcast(
    id: Long? = null,
    categoryId: Long = 2,
    status: BroadcastStatus = BroadcastStatus.LIVE,
    videoKey: String = "video_key",
    memberId: Long = 1,
    title: String = "title",
    url: String = "url",
    thumbnail: String = "thumbnail",
    notice: String = "notice",
    pin: String = "pin",
    heartCount: Int = Integer.valueOf(123),
    startedAt: ZonedDateTime = ZonedDateTime.now(),
    endedAt: ZonedDateTime? = null
): Broadcast {
    return Broadcast(
        id,
        categoryId,
        status,
        videoKey,
        memberId,
        title,
        url,
        thumbnail,
        notice,
        pin,
        heartCount,
        startedAt,
        endedAt,
        arrayListOf()
    )
}

fun makeInfluencer(
    id: Long,
    status: InfluencerStatus = InfluencerStatus.ACTIVE,
    broadcastCount: Int = 0,
    earnedAt: ZonedDateTime = ZonedDateTime.now()
) : Influencer {
    return Influencer(
        id,
        status,
        broadcastCount,
        earnedAt
    )
}

fun makeViewer(
    id : Long? = null,
    broadcast: Broadcast? = null,
    type: BroadcastViewerType? = BroadcastViewerType.MEMBER,
    status: BroadcastViewerStatus? = BroadcastViewerStatus.ACTIVE,
    isSuspended: Boolean = false,
    joinedAt: ZonedDateTime = ZonedDateTime.now(),
    member: Member
): BroadcastViewer {
    return BroadcastViewer(
        id,
        broadcast,
        member.id,
        if (StringUtils.isBlank(member.username)) "GUEST_0001" else ViewerUsernameUtil.generateSortedUsername(member.username),
        type,
        status,
        isSuspended,
        joinedAt
    )
}