package com.jocoos.mybeautip.global.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Telecom implements CodeValue {
    SKT(false, "SKT"),
    KT(false, "KT"),
    LG(false, "LG U+"),
    SKT_SAVE(true, "SKT 알뜰폰"),
    KT_SAVE(true, "KT 알뜰폰"),
    LG_SAVE(true, "LG U+ 알뜰폰");

    private final boolean isSave;
    private final String description;

}
