package com.jocoos.mybeautip.domain.community.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LikeResponse {

    private Boolean isLike;

    private Integer likeCount;

}
