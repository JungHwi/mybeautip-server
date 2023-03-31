package com.jocoos.mybeautip.client.flipfloplite.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FFLChatRoomBroadcastMessageCustomType {

    MSG("일반 알림", null),
    CLOSE("채팅방 종료 알림", null),
    DISCONNECT("채팅방 강제 닫음 알림", null),
    UPDATE("라이브 관련 내용 업데이트 알림", null),
    ACTIVE("라이브 시작 알림", null),
    INACTIVE("라이브 일시 중단 알림", null),

    // CUSTOM
    VISIBLE_MESSAGE("메세지 보여지게", null),
    INVISIBLE_MESSAGE("메세지 안 보여지게", null),
    CHAT("채팅방 활성화", "채팅창이 활성화되었습니다."),
    NO_CHAT("채팅방 정지", "라이브 운영자에 의해 채팅창이 정지되었습니다."),
    UPDATE_BROADCAST("방송 정보 변경", null),
    UPDATE_STATUS("방송 상태 변경", null),
    PIN("채팅 고정", null),
    NO_PIN("채팅 고정 해제", null),
    HEART_COUNT("좋아요수", null),
    URL_CHANGED("방송 URL 변경", null)
    ;

    private final String description;
    private final String sendMessage;
}
