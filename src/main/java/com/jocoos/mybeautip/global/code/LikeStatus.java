package com.jocoos.mybeautip.global.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LikeStatus implements CodeValue {

    LIKE("좋아요"),
    UNLIKE("좋아요 취소");

    private final String description;

    @Override
    public String getName() {
        return this.name();
    }
}
