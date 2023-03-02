package com.jocoos.mybeautip.testutil.fixture

import com.jocoos.mybeautip.domain.broadcast.code.BroadcastCategoryType
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastCategoryType.CHAT
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus.LIVE
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus.SCHEDULED
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastViewerStatus
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastViewerType
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastCategory
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastViewer
import com.jocoos.mybeautip.domain.broadcast.service.util.ViewerUsernameUtil
import com.jocoos.mybeautip.domain.member.code.InfluencerStatus
import com.jocoos.mybeautip.domain.member.persistence.domain.Influencer
import com.jocoos.mybeautip.member.Member
import org.apache.tika.utils.StringUtils
import java.time.ZonedDateTime

fun makeBroadcast(
    category: BroadcastCategory,
    status: BroadcastStatus = LIVE,
    memberId: Long = 1,
    videoKey: Long = 1,
    title: String = "title",
    thumbnail: String = "thumbnail",
    notice: String = "notice",
    startedAt: ZonedDateTime = ZonedDateTime.now()
): Broadcast {
    return Broadcast(videoKey, memberId, status, title, thumbnail, notice, startedAt, category)
}

fun makeBroadcastCategory(
    parentId: Long?,
    type: BroadcastCategoryType = CHAT,
    title: String = "title",
    description: String = "description"
) : BroadcastCategory {
    return BroadcastCategory(parentId, type, title, description)
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
    suspendedAt: ZonedDateTime? = null,
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
        suspendedAt,
        joinedAt
    )
}
