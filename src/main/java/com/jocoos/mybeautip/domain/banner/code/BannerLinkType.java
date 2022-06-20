package com.jocoos.mybeautip.domain.banner.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BannerLinkType implements CodeValue {

    EVENT("Event 화면");

    private final String description;
}
