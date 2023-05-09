package com.jocoos.mybeautip.global.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CountryCode implements CodeValue {

    KR("대한민국"),
    TH("태국"),
    VN("베트남");

    private final String description;

    @Override
    public String getName() {
        return this.name();
    }
}
