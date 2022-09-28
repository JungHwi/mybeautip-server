package com.jocoos.mybeautip.domain.video.dto;

import com.jocoos.mybeautip.domain.video.code.VideoCategoryType;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VideoCategoryResponse {

    private Integer id;

    private VideoCategoryType type;

    private String title;

    private String shapeUrl;
}
