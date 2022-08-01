package com.jocoos.mybeautip.domain.community.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommunityMemberResponse {

    private Long id;

    private String username;

    private String avatarUrl;
}
