package com.jocoos.mybeautip.domain.scrap.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ScrapType implements CodeValue {

    VIDEO("동영상"),
    COMMUNITY("커뮤니티");

    private final String description;


    @Override
    public String getName() {
        return this.name();
    }
}
