package com.jocoos.mybeautip.global.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UrlDirectory implements CodeValue {

    SHARE("친구 초대 공유", "share/"),
    AVATAR("아바타", "avatar/"),
    COMMUNITY("커뮤니티", "community/%s/"),
    VIDEO_CATEGORY("비디오 카테고리", "video_category/"),
    EVENT("이벤트", "event/"),
    EVENT_PRODUCT("이벤트 상품", "event/product/"),
    POPUP("팝업", "popup/"),
    PLACARD("플래카드", "placard/"),
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
