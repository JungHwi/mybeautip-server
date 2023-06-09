package com.jocoos.mybeautip.domain.video.vo;

import com.jocoos.mybeautip.domain.video.code.VideoStatus;
import com.jocoos.mybeautip.global.code.SearchField;
import com.jocoos.mybeautip.global.vo.SearchOption;
import com.jocoos.mybeautip.video.Visibility;
import lombok.Builder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Date;

@Builder
public record AdminVideoSearchCondition(VideoStatus status,
                                        Integer categoryId,
                                        Visibility visibility,
                                        Boolean isRecommended,
                                        Pageable pageable,
                                        SearchOption searchOption) {
    public Sort sort() {
        return pageable.getSort();
    }

    public long offset() {
        return pageable.getOffset();
    }

    public long limit() {
        return pageable.getPageSize();
    }

    public Date startAtDate() {
        return searchOption == null ? null : searchOption.getStartAtDate();
    }

    public Date endAtDate() {
        return searchOption == null ? null : searchOption.getEndAtDate();
    }

    public Boolean isReported() {
        return searchOption == null ? null : searchOption.getIsReported();
    }

    public boolean isSearchFieldEqual(SearchField searchField) {
        if (searchOption == null) {
            return false;
        }
        return searchOption.isSearchFieldEqual(searchField);
    }

    public String keyword() {
        return searchOption.getKeyword();
    }

    public Boolean isTopFix() {
        return searchOption == null ? null : searchOption.getIsTopFix();
    }
}
