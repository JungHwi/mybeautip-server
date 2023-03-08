package com.jocoos.mybeautip.client.flipfloplite.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FFLChatRoomBroadcastMessageCustomType {
    MSG("일반 알림"),
    CLOSE("채팅방 종료 알림"),
    DISCONNECT("채팅방 강제 닫음 알림"),
    UPDATE("라이브 관련 내용 업데이트 알림"),
    ACTIVE("라이브 시작 알림"),
    INACTIVE("라이브 일시 중단 알림");

    private final String description;
}
