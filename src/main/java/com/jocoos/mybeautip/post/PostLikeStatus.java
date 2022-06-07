package com.jocoos.mybeautip.post;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PostLikeStatus implements CodeValue {

    LIKE(""),
    UNLIKE("");

    private final String description;
}
