package com.jocoos.mybeautip.domain.notification.client.vo;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BaseMessageInfo {

    private String title;
    private String content;
    private String imageUrl;
    private String deepLink;
}
