package com.jocoos.mybeautip.client.flipfloplite.code;

import com.jocoos.mybeautip.global.exception.BadRequestException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Getter
@RequiredArgsConstructor
public enum FFLCallbackType {

    // Video Room Status Change
    VIDEO_ROOM_SCHEDULED,
    VIDEO_ROOM_LIVE,
    VIDEO_ROOM_LIVE_INACTIVE,
    VIDEO_ROOM_ENDED,

    // Stream Key Status Change
    STREAM_KEY_ACTIVE_PREP,
    STREAM_KEY_ACTIVE_LIVE_PREP,
    STREAM_KEY_ACTIVE,
    STREAM_KEY_ACTIVE_LIVE,
    STREAM_KEY_INACTIVE_LIVE,
    STREAM_KEY_INACTIVE
    ;


    public FFLVideoRoomState toVideoRoomState() {
        return switch (this) {
            case VIDEO_ROOM_LIVE -> FFLVideoRoomState.LIVE;
            case VIDEO_ROOM_LIVE_INACTIVE -> FFLVideoRoomState.LIVE_INACTIVE;
            default -> throw new BadRequestException("Invalid video room state");
        };
    }

    @RequiredArgsConstructor
    public enum FFLCallbackRequestType {
        VIDEO_ROOM_STATUS_CHANGE(Set.of(VIDEO_ROOM_LIVE, VIDEO_ROOM_LIVE_INACTIVE, VIDEO_ROOM_SCHEDULED, VIDEO_ROOM_ENDED)),
        STREAM_KEY_STATUS_CHANGE(Set.of(STREAM_KEY_ACTIVE_PREP, STREAM_KEY_ACTIVE_LIVE_PREP, STREAM_KEY_ACTIVE, STREAM_KEY_ACTIVE_LIVE, STREAM_KEY_INACTIVE_LIVE, STREAM_KEY_INACTIVE))
        ;
        private final Set<FFLCallbackType> types;

        public static FFLCallbackRequestType getRequestType(FFLCallbackType type) {
            if (VIDEO_ROOM_STATUS_CHANGE.types.contains(type))
                return VIDEO_ROOM_STATUS_CHANGE;
            if (STREAM_KEY_STATUS_CHANGE.types.contains(type))
                return STREAM_KEY_STATUS_CHANGE;
            throw new BadRequestException("");
        }
    }
}
