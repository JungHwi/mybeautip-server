package com.jocoos.mybeautip.domain.broadcast.dto;

import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastCategory;
import lombok.Getter;

@Getter
public class BroadcastCategoryResponse {
    private final long id;
    private final String title;

    public BroadcastCategoryResponse(BroadcastCategory category) {
        this.id = category.getId();
        this.title = category.getTitle();
    }
}
