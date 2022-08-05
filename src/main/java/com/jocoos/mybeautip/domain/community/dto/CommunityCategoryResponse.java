package com.jocoos.mybeautip.domain.community.dto;

import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommunityCategoryResponse {

    private Long id;

    private CommunityCategoryType type;

    private String title;

    private String hint;

}
