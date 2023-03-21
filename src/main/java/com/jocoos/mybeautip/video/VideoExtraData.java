package com.jocoos.mybeautip.video;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoExtraData {
    private String goods;
    private String category;
    private String startedAt;
    private Long communityId;
    private String title;
    private String content;
    private List<String> products;
}
