package com.jocoos.mybeautip.member.detail;

import com.jocoos.mybeautip.member.code.SkinType;
import com.jocoos.mybeautip.member.code.SkinWorry;
import lombok.Builder;

import java.util.Set;

@Builder
public class MemberDetailResponse {

    private int ageGroup;

    private SkinType skinType;

    private Set<SkinWorry> skinWorry;

    private String inviterTag;
}
