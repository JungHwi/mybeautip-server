package com.jocoos.mybeautip.global.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SearchField {

    USERNAME("username"),
    PHONE_NUMBER("phoneNumber"),
    EMAIL("email");

    private final String fieldName;
}
