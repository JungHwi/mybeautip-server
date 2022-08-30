package com.jocoos.mybeautip.domain.notification.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TemplateType implements CodeValue {
    VIDEO_UPLOAD("동영상 업로드 시, 모든 유저에게"),
    COMMUNITY_COMMENT("글에 댓글이 달렸을때, 글 작성자에게"),
    COMMUNITY_LIKE_1("글에 하트가 처음 달렸을 때, 글 작성자에게"),
    COMMUNITY_LIKE_20("글에 하트가 20개 달렸을 때, 글 작성자에게"),
    COMMUNITY_COMMENT_REPLY("댓글에 대댓글이 달렸을 때, 댓글 작성자에게"),
    NO_LOGIN_2WEEKS("로그인 안 한지 2주째...");

    private final String description;

    @Override
    public String getName() {
        return this.name();
    }
}


