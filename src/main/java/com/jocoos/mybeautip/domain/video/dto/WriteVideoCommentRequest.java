package com.jocoos.mybeautip.domain.video.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class WriteVideoCommentRequest {
    private final String comment;
    private final Long parentId;
}
