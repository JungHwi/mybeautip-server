package com.jocoos.mybeautip.global.vo;

import lombok.Builder;
import lombok.Getter;

import static com.jocoos.mybeautip.global.constant.SignConstant.SIGN_AT;

@Getter
@Builder
public class Email {

    private String domain;

    private String account;

    public String getEmail() {
        return account + SIGN_AT + domain;
    }

    public String toString() {
        return getEmail();
    }
}
