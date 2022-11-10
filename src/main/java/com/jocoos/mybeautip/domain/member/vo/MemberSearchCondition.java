package com.jocoos.mybeautip.domain.member.vo;

import com.jocoos.mybeautip.domain.member.code.GrantType;
import com.jocoos.mybeautip.domain.member.code.MemberStatus;
import com.jocoos.mybeautip.global.vo.SearchOption;
import lombok.Builder;
import org.springframework.data.domain.Pageable;

import java.util.Date;

@Builder
public record MemberSearchCondition(MemberStatus status,
                                    Pageable pageable,
                                    SearchOption searchOption,
                                    GrantType grantType) {
    public long getOffset() {
        return pageable.getOffset();
    }

    public long getSize() {
        return pageable.getPageSize();
    }

    public String getKeyword() {
        return searchOption == null ? null : searchOption.getKeyword();
    }

    public Date getStartAt() {
        return searchOption == null ? null : searchOption.getStartAtDate();
    }

    public Date getEndAt() {
        return searchOption == null ? null : searchOption.getEndAtDate();
    }

    public boolean isBlocked() {
        if (searchOption == null) {
            return false;
        }
        return searchOption.getIsReported() != null && searchOption.getIsReported();
    }
}
