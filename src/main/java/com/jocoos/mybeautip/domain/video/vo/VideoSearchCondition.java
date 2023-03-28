package com.jocoos.mybeautip.domain.video.vo;

import com.jocoos.mybeautip.domain.search.vo.KeywordSearchRequest;
import com.jocoos.mybeautip.domain.video.code.VideoCategoryType;
import com.jocoos.mybeautip.domain.video.dto.VideoCategoryResponse;
import lombok.Builder;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.time.ZonedDateTime;
import java.util.Date;

@Getter
public class VideoSearchCondition {
    private final String keyword;
    private final Integer categoryId;
    private final Date cursor;
    private final int size;
    private final Boolean isRecommended;

    @Builder
    public VideoSearchCondition(String keyword,
                                 VideoCategoryResponse category,
                                 ZonedDateTime cursor,
                                int size,
                                Boolean isRecommended) {
        this.size = size;
        this.isRecommended = isRecommended;
        this.keyword = setKeyword(keyword);
        this.categoryId = setCategoryId(category);
        this.cursor = setDateCursor(cursor);
    }

    public static VideoSearchCondition from(KeywordSearchRequest condition) {
        return VideoSearchCondition.builder()
                .keyword(condition.keyword())
                .cursor(condition.dateCursor())
                .size(condition.size())
                .build();
    }

    private String setKeyword(String keyword) {
        return StringUtils.hasText(keyword) ? keyword.trim() : null;
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

    public boolean isCategorySearch() {
        return categoryId != null;
    }
}
