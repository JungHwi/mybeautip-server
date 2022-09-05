package com.jocoos.mybeautip.global.exception;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode implements CodeValue {

    // COMMON
    BAD_REQUEST("잘못된 요청"),
    NOT_FOUND_MEMBER("회원정보 없음"),

    // MEMBER
    ALREADY_USED("회원명 이미 사용중"),
    BANNED_WORD("회원명에 금칙어 포함");

    private final String description;

    public String getKey() {
        return this.name().toLowerCase();
    }

    @Override
    public String getName() {
        return this.name();
    }
}
