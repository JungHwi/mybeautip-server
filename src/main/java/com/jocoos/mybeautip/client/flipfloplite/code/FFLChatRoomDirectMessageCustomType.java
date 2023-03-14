package com.jocoos.mybeautip.client.flipfloplite.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FFLChatRoomDirectMessageCustomType {
    DM("일반 알림"),
    BLOCK("채팅방 종료 알림"),
    ALERT("채팅방 강제 닫음 알림"),

    // CUSTOM
    MANAGER("매니저 설정"),
    MANAGER_OUT("매니저 퇴장"),
    NO_MANAGER("매니저 설정"),
    CHAT("채팅 가능"),
    NO_CHAT("채팅 불가능"),
    EXILE("퇴장");

    private final String description;
}
