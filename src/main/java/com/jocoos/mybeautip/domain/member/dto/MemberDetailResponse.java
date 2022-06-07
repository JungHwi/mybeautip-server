package com.jocoos.mybeautip.domain.member.dto;

import com.jocoos.mybeautip.member.code.SkinType;
import com.jocoos.mybeautip.member.code.SkinWorry;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
public class MemberDetailResponse {

    private int ageGroup;

    private SkinType skinType;

    private Set<SkinWorry> skinWorry;

    private String inviterTag;
}
