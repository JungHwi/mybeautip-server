package com.jocoos.mybeautip.member.detail;

import com.jocoos.mybeautip.member.code.SkinType;
import com.jocoos.mybeautip.member.code.SkinWorry;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Builder
public class MemberDetailRequest {

    @Setter
    private long memberId;

    private int ageGroup;

    private SkinType skinType;

    private Set<SkinWorry> skinWorry;

    private String inviterTag;

}
