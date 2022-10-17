package com.jocoos.mybeautip.domain.video.vo;

import com.jocoos.mybeautip.domain.search.vo.KeywordSearchCondition;
import com.jocoos.mybeautip.domain.video.code.VideoCategoryType;
import com.jocoos.mybeautip.domain.video.dto.VideoCategoryResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.Date;

@Getter
public class VideoSearchCondition {
    private final String keyword;
    private final Integer categoryId;
    private final Date cursor;
    private final int size;

    @Builder
    public VideoSearchCondition(String keyword, VideoCategoryResponse category, ZonedDateTime cursor, int size) {
        this.size = size;
        this.keyword = setKeyword(keyword);
        this.categoryId = setCategoryId(category);
        this.cursor = setDateCursor(cursor);
    }

    public static VideoSearchCondition from(KeywordSearchCondition condition) {
        return VideoSearchCondition.builder()
                .keyword(condition.getKeyword())
                .cursor(condition.getCursor())
                .size(condition.getSize())
                .build();
    }

    private String setKeyword(String keyword) {
        return keyword == null ? null : keyword.trim();
    }

    private Integer setCategoryId(VideoCategoryResponse category) {
        if (category == null || category.getType() == VideoCategoryType.GROUP) {
            return null;
        }
        return category.getId();
    }

    private Date setDateCursor(ZonedDateTime cursor) {
        return cursor == null ? null : Date.from(cursor.toInstant());
    }
}
