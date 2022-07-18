package com.jocoos.mybeautip.domain.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class MemberInvitationInfoResponse {

    private final String title;

    private final String description;

    @Setter private String shareSquareImageUrl;

    @Setter private String shareRectangleImageUrl;

}
