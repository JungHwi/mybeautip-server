package com.jocoos.mybeautip.global.code;

import com.jocoos.mybeautip.domain.file.code.FileType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UrlDirectory implements CodeValue {

    SHARE("친구 초대 공유", "share/"),
    AVATAR("아바타", "avatar/"),
    COMMUNITY("커뮤니티", "community/%s/"),
    COMMUNITY_COMMENT("커뮤니티 댓글", "community_comment/%s/"),
    VIDEO_CATEGORY("비디오 카테고리", "video_category/"),
    VIDEO_COMMENT("비디오 댓글", "video_comment/%s/"),
    EVENT("이벤트", "event/"),
    EVENT_PRODUCT("이벤트 상품", "event/product/"),
    POPUP("팝업", "popup/"),
    PLACARD("플래카드", "placard/"),
    TEMP_IMAGE("임시 이미지 폴더", "temp/image/"),
    TEMP_VIDEO("임시 비디오 폴더", "temp/video/")
    ;

    private final String description;
    private final String directory;

    @Override
    public String getName() {
        return this.name();
    }

    public String getDirectory(Long id) {
        switch (this) {
            case COMMUNITY, COMMUNITY_COMMENT, VIDEO_COMMENT:
                return String.format(this.directory, id);
            default:
                return this.directory;
        }
    }

    public static String getDirectory(FileType type) {
       return switch (type) {
            case IMAGE -> TEMP_IMAGE.directory;
           case VIDEO -> TEMP_VIDEO.directory;
        };
    }
}
