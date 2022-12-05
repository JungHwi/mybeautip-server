package com.jocoos.mybeautip.domain.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimpleMemberInfo {
    private Long id;
    private String email;
    private String username;
    private String avatarUrl;
}
