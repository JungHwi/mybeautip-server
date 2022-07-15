package com.jocoos.mybeautip.domain.member.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberInvitationInfoResponse {

    private final String title;

    private final String description;

    private final String shareSquareImageUrl;

    private final String shareRectangleImageUrl;


    @Builder
    public MemberInvitationInfoResponse(String title,
                                        String description,
                                        String shareSquareImageUrl,
                                        String shareRectangleImageUrl) {
        this.title = title;
        this.description = description;
        this.shareSquareImageUrl = shareSquareImageUrl;
        this.shareRectangleImageUrl = shareRectangleImageUrl;
    }
}
