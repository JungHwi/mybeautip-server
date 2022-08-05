package com.jocoos.mybeautip.domain.community.dto;

import com.jocoos.mybeautip.domain.member.code.MemberStatus;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommunityMemberResponse {

    private Long id;

    private MemberStatus status;

    private String username;

    private String avatarUrl;
}
