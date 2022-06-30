package com.jocoos.mybeautip.domain.member.vo;

import com.jocoos.mybeautip.member.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChangedTagInfo {
    private boolean isChanged;
    private Member member;
    private Member targetMember;
}
