package com.jocoos.mybeautip.domain.community.dto;

import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCategory;
import com.querydsl.core.annotations.QueryProjection;
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

    @QueryProjection
    public CommunityCategoryResponse(Long id, CommunityCategoryType type, String title) {
        this.id = id;
        this.type = type;
        this.title = title;
    }

    public static CommunityCategoryResponse from(CommunityCategory category) {
        return new CommunityCategoryResponse(category.getId(), category.getType(), category.getTitle());
    }
}
