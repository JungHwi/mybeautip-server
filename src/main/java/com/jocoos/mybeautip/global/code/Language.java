package com.jocoos.mybeautip.global.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Language implements CodeValue {

    KO("한국어"),
    EN("영어");

    private final String description;

    public Language getDefault() {
        return Language.KO;
    }
}
