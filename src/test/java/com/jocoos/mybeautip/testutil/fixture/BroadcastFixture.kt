package com.jocoos.mybeautip.testutil.fixture

import com.jocoos.mybeautip.client.flipfloplite.code.FFLChatRoomBroadcastMessageCustomType
import com.jocoos.mybeautip.client.flipfloplite.code.FFLChatRoomBroadcastMessageType
import com.jocoos.mybeautip.client.flipfloplite.dto.FFLBroadcastMessageRequest
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastReportType
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastViewerStatus
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastViewerType
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastCategory
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastPinMessage
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastReport
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastViewer
import com.jocoos.mybeautip.domain.broadcast.service.util.ViewerUsernameUtil
import com.jocoos.mybeautip.domain.member.code.InfluencerStatus
import com.jocoos.mybeautip.domain.member.persistence.domain.Influencer
import com.jocoos.mybeautip.member.Member
import org.apache.tika.utils.StringUtils
import java.time.ZonedDateTime

fun makeBroadcast(
    category: BroadcastCategory,
    memberId: Long = 1,
    isStartNow: Boolean = false,
    videoKey: Long = 1,
    channelKey: String = "channelKey",
    title: String = "title",
    thumbnail: String = "thumbnail",
    notice: String = "notice",
    startedAt: ZonedDateTime = ZonedDateTime.now().plusDays(1)
): Broadcast {
    val broadcast = Broadcast(memberId, title, thumbnail, startedAt, category, notice, isStartNow)
    broadcast.updateVideoAndChannelKey(videoKey, channelKey)
    return broadcast
}

fun makeBroadcastCategory(
    parentId: Long?,
    title: String = "title",
    description: String = "description"
) : BroadcastCategory {
    return BroadcastCategory(parentId, title, description)
}

fun makeBroadcastReport(
    broadcast: Broadcast,
    reporterId: Long,
    reason: String = "신고 사유"
) : BroadcastReport {
    return BroadcastReport(BroadcastReportType.BROADCAST, broadcast.id, reporterId, broadcast.memberId, reason, "")
}

fun makeMessageReport(
    broadcast: Broadcast,
    reporterId: Long,
    reportedId: Long,
    reason: String = "신고 사유",
    description: String = "대화 내용"
) : BroadcastReport {
    return BroadcastReport(BroadcastReportType.MESSAGE, broadcast.id, reporterId, reportedId, reason, description)
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

fun makeMessage(
    messageType: FFLChatRoomBroadcastMessageType? = FFLChatRoomBroadcastMessageType.MESSAGE,
    customType: FFLChatRoomBroadcastMessageCustomType? = FFLChatRoomBroadcastMessageCustomType.MSG,
    message: String? = "test message",
    data: Map<String, Any>? = mapOf("test" to "test"),
    appUserIds: List<String>? = null
): FFLBroadcastMessageRequest {
    return FFLBroadcastMessageRequest(
        messageType,
        customType,
        message,
        data,
        appUserIds
    )
}

fun makePinMessage(
    broadcast: Broadcast,
    memberId: Long = 3,
    username: String = "username",
    avatarUrl: String = "avatarUrl",
    messageId: Long = 1,
    message: String = "message"
) : BroadcastPinMessage {
    return BroadcastPinMessage(broadcast, memberId, username, avatarUrl, messageId, message)
}
