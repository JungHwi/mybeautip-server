package com.jocoos.mybeautip.testutil.fixture

import com.jocoos.mybeautip.client.flipfloplite.code.FFLAccessLevel
import com.jocoos.mybeautip.client.flipfloplite.code.FFLCreatorType
import com.jocoos.mybeautip.client.flipfloplite.code.FFLEntityState
import com.jocoos.mybeautip.client.flipfloplite.code.FFLVideoFormat
import com.jocoos.mybeautip.client.flipfloplite.code.FFLVideoRoomState
import com.jocoos.mybeautip.client.flipfloplite.code.FFLVideoRoomState.LIVE
import com.jocoos.mybeautip.client.flipfloplite.code.FFLVideoRoomType
import com.jocoos.mybeautip.client.flipfloplite.dto.FFLAppInfo
import com.jocoos.mybeautip.client.flipfloplite.dto.FFLChatInfo
import com.jocoos.mybeautip.client.flipfloplite.dto.FFLMemberInfo
import com.jocoos.mybeautip.client.flipfloplite.dto.FFLStreamKey
import com.jocoos.mybeautip.client.flipfloplite.dto.FFLUserInfo
import com.jocoos.mybeautip.client.flipfloplite.dto.FFLVideoRoomResponse
import com.jocoos.mybeautip.client.flipfloplite.dto.FFLVideoRoomStatistics
import org.springframework.data.annotation.LastModifiedBy
import java.time.ZonedDateTime
import java.time.ZonedDateTime.now

fun makeFFLVideoRoomResponse(
    id: Long = 1,
    scheduledAt: ZonedDateTime = now().plusDays(1),
    liveUrl: String = "liveUrl",
    lastModifiedAt: ZonedDateTime = now(),
    state: FFLEntityState? = null,
    videoRoomState: FFLVideoRoomState = LIVE,
    type: FFLVideoRoomType? = null,
    format: FFLVideoFormat? = null,
    app: FFLAppInfo? = null,
    member: FFLMemberInfo? = null,
    creatorType: FFLCreatorType? = null,
    creatorId: Long? = null,
    accessLevel: FFLAccessLevel? = null,
    title: String? = null,
    description: String? = null,
    streamKey: FFLStreamKey? = null,
    chat: FFLChatInfo? = null,
    stats: FFLVideoRoomStatistics? = null,
    createdAt: ZonedDateTime? = null,
    createdBy: FFLUserInfo? = null,
    lastModifiedBy: FFLUserInfo? = null
): FFLVideoRoomResponse {
    return FFLVideoRoomResponse(
        id,
        state,
        videoRoomState,
        type,
        format,
        app,
        member,
        creatorType,
        creatorId,
        accessLevel,
        title,
        description,
        scheduledAt,
        streamKey,
        liveUrl,
        chat,
        stats,
        createdAt,
        lastModifiedAt,
        createdBy,
        lastModifiedBy
    )
}
