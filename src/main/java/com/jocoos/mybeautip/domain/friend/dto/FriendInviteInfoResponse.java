package com.jocoos.mybeautip.domain.friend.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class FriendInviteInfoResponse {

    private final String title;

    private final String description;

    private final String shareSquareImageUrl;

    private final String shareRectangleImageUrl;


    @Builder
    public FriendInviteInfoResponse(String title,
                                    String description,
                                    String shareSquareImageUrl,
                                    String shareRectangleImageUrl) {
        this.title = title;
        this.description = description;
        this.shareSquareImageUrl = shareSquareImageUrl;
        this.shareRectangleImageUrl = shareRectangleImageUrl;
    }
}
