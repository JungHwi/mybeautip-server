package com.jocoos.mybeautip.global.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UrlDirectory implements CodeValue {

    SHARE("share/");

    private final String description;

    @Override
    public String getName() {
        return this.name();
    }
}
