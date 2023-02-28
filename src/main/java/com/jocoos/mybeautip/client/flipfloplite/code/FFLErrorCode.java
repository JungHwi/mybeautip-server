package com.jocoos.mybeautip.client.flipfloplite.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.EnumUtils;

@Getter
@AllArgsConstructor
public enum FFLErrorCode {
    FFL_ERROR("Default Error Code"),
    // CREDENTIALS
    EMPTY_APP_CREDENTIALS("인증 정보 없음"),
    INVALID_APP_CREDENTIALS("잘못된 인증 정보"),
    // USER
    EMPTY_APP_USER_ID("appUserId 필수 요청 파라메터가 누락되었을 경우"),
    MEMBER_NOT_FOUND("Member 리소스가 존재하지 않을 경우"),
    // VIDEO ROOM
    VIDEO_ROOM_NOT_FOUND("VideoRoom 리소스가 존재하지 않을 경우"),
    VIDEO_ROOM_ACCESS_DENIED("액세스 레벨 정책에 따라 요청 권한이 부족할 경우"),
    EMPTY_VIDEO_ROOM_APP_USER_ID("videoRoom.appUserId 없음"),
    EMPTY_VIDEO_ROOM_TYPE("videoRoom.type 없음"),
    EMPTY_VIDEO_ROOM_SCHEDULED_AT("scheduledAt 없음"),
    VIDEO_ROOM_TYPE_NOT_BROADCAST_RTMP("VideoRoom의 type = BROADCAST_RTMP가 아닐 경우"),
    VIDEO_ROOM_STATE_NOT_SCHEDULED("VideoRoom의 videoRoomState = SCHEDULED가 아닐 경우"),
    VIDEO_ROOM_STATE_NOT_LIVE_INACTIVE("VideoRoom의 videoRoomState = LIVE_INACTIVE가 아닐 경우"),
    // STREAM KEY
    STREAM_KEY_NOT_FOUND("StreamKey 리소스가 존재하지 않을 경우"),
    STREAM_KEY_STATE_NOT_INACTIVE("StreamKey.streamKeyState = INACIVE가 아닐 경우"),
    STREAM_KEY_STATE_NOT_INACTIVE_LIVE("StreamKey의 streamKeyState = INACTIVE_LIVE가 아닐 경우"),
    STREAM_KEY_STATE_NOT_ACTIVE("StreamKey의 streamKeyState = ACTIVE가 아닐 경우");

    private final String description;

    public static FFLErrorCode of(String errorCode) {
        return EnumUtils.getEnum(FFLErrorCode.class, errorCode, FFLErrorCode.FFL_ERROR);
    }
}
