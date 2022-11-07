package com.jocoos.mybeautip.domain.member.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberIdAndUsernameResponse {
    private final Long id;
    private final String username;
}
