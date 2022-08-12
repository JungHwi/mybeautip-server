package com.jocoos.mybeautip.global.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UrlDirectory implements CodeValue {

    SHARE("친구 초대 공유", "share/"),
    AVATAR("아바타", "avatar/"),
    COMMUNITY("커뮤니티", "community/%s/"),
    EVENT("이벤트", "event/"),
    EVENT_PRODUCT("이벤트 상품", "event/product/"),
    TEMP("임시 폴더", "temp/");

    private final String description;
    private final String directory;

    @Override
    public String getName() {
        return this.name();
    }

    public String getDirectory(Long id) {
        switch (this) {
            case COMMUNITY:
                return String.format(this.directory, String.valueOf(id));
            default:
                return this.directory;
        }
    }
}
