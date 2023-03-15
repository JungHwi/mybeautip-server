package com.jocoos.mybeautip.domain.member.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberRegistrationRequest {

    private Long id;
    private String username;
    private String avatarUrl;
}
