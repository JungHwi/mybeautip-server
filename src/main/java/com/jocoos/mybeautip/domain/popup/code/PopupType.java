package com.jocoos.mybeautip.domain.popup.code;


import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PopupType implements CodeValue {

    SIGNUP("회원 가입한 뒤"),
    COMEBACK("간만에 로그인"),
    LOGIN("로그인 한 뒤"),
    NOTICE("공지 사항"),
    WAKEUP("휴면 복귀");

    private final String description;

    @Override
    public String getName() {
        return this.name();
    }
}
