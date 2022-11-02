package com.jocoos.mybeautip.domain.member.vo;

import com.jocoos.mybeautip.domain.member.code.GrantType;
import com.jocoos.mybeautip.domain.member.code.MemberStatus;
import com.jocoos.mybeautip.global.vo.SearchKeyword;
import lombok.Builder;
import org.springframework.data.domain.Pageable;

import java.util.Date;

@Builder
public record MemberSearchCondition(MemberStatus status,
                                    Pageable pageable,
                                    SearchKeyword searchKeyword,
                                    GrantType grantType) {
    public long getOffset() {
        return pageable.getOffset();
    }

    public long getSize() {
        return pageable.getPageSize();
    }

    public String getKeyword() {
        return searchKeyword == null ? null : searchKeyword.getKeyword();
    }

    public Date getStartAt() {
        return searchKeyword == null ? null : searchKeyword.getStartAtDate();
    }

    public Date getEndAt() {
        return searchKeyword == null ? null : searchKeyword.getEndAtDate();
    }
}
