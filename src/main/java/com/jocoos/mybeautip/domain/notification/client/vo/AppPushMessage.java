package com.jocoos.mybeautip.domain.notification.client.vo;

import com.jocoos.mybeautip.domain.notification.code.MessageType;
import com.jocoos.mybeautip.global.util.MessageConvertUtil;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class AppPushMessage {

    private MessageType messageType;
    private String title;
    private String message;
    private String imageUrl;
    private String deepLink;

    public AppPushMessage setArguments(Map<String, String> arguments) {
        return AppPushMessage.builder()
                .messageType(this.messageType)
                .imageUrl(this.imageUrl)
                .title(MessageConvertUtil.generateStringByArguments(this.title, arguments))
                .message(MessageConvertUtil.generateStringByArguments(this.message, arguments))
                .deepLink(MessageConvertUtil.generateStringByArguments(this.deepLink, arguments))
                .build();
    }
}
