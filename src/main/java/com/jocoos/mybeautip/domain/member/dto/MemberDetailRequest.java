package com.jocoos.mybeautip.domain.member.dto;

import com.jocoos.mybeautip.member.code.SkinType;
import com.jocoos.mybeautip.member.code.SkinWorry;
import lombok.*;

import java.util.Set;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDetailRequest {

    @Setter
    private long memberId;

    private int ageGroup;

    private SkinType skinType;

    private Set<SkinWorry> skinWorry;

    private String inviterTag;

}
