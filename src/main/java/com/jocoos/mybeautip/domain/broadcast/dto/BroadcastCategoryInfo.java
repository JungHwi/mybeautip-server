package com.jocoos.mybeautip.domain.broadcast.dto;

import com.jocoos.mybeautip.domain.broadcast.code.BroadcastCategoryType;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastCategory;
import lombok.Getter;

@Getter
public class BroadcastCategoryInfo {
    private final long id;
    private final BroadcastCategoryType type;
    private final String description;

    public BroadcastCategoryInfo(BroadcastCategory category) {
        this.id = category.getId();
        this.type = category.getType();
        this.description = category.getDescription();
    }
}
