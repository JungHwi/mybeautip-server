package com.jocoos.mybeautip.client.flipfloplite.dto;

import com.jocoos.mybeautip.client.flipfloplite.code.*;

import java.time.ZonedDateTime;

public record FFLVideoRoomResponse(Long id,
                                   FFLEntityState state,
                                   FFLVideoRoomState videoRoomState,
                                   FFLVideoRoomType type,
                                   FFLVideoFormat format,
                                   FFLAppInfo app,
                                   FFLMemberInfo member,
                                   FFLCreatorType creatorType,
                                   Long creatorId,
                                   FFLAccessLevel accessLevel,
                                   String title,
                                   String description,
                                   ZonedDateTime scheduledAt,
                                   FFLStreamKey streamKey,
                                   String liveUrl,
                                   FFLChatInfo chat,
                                   FFLVideoRoomStatistics stats,
                                   ZonedDateTime createdAt,
                                   ZonedDateTime lastModifiedAt,
                                   FFLUserInfo createdBy,
                                   FFLUserInfo lastModifiedBy) {
}
