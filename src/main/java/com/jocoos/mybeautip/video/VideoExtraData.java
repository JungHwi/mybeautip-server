package com.jocoos.mybeautip.video;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VideoExtraData {
    private String goods;
    private String category;
    private String startedAt;
    private Long communityId;
    private String title;
    private String content;
}
