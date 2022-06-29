package com.jocoos.mybeautip.domain.popup.code;


import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PopupType implements CodeValue {

    SIGNUP("회원가입 한 뒤"),
    LOGIN("로그인 한 뒤");

    private final String description;
}
