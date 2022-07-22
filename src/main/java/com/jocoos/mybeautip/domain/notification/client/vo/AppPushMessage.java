package com.jocoos.mybeautip.domain.notification.client.vo;

import com.jocoos.mybeautip.domain.notification.code.NotificationLinkType;
import com.jocoos.mybeautip.domain.notification.vo.NotificationLink;
import com.jocoos.mybeautip.global.util.NotificationConvertUtil;
import lombok.Builder;
import lombok.Getter;
import net.minidev.json.annotate.JsonIgnore;

import java.util.List;
import java.util.Map;

@Getter
@Builder
public class AppPushMessage {

//    private MessageType messageType; // ios 에서 message_type 이 예약어라서 필요하면 이름 바꿔야 함.
    private String title;
    private String message;
    private String imageUrl;
    private Long notificationId;
    private List<NotificationLink> notificationLink;

    @JsonIgnore
    private List<NotificationLinkType> notificationLinkType;

    public AppPushMessage setArguments(Map<String, String> arguments) {
        return AppPushMessage.builder()
//                .messageType(this.messageType)
                .imageUrl(this.imageUrl)
                .notificationId(this.notificationId)
                .title(NotificationConvertUtil.generateStringByArguments(this.title, arguments))
                .message(NotificationConvertUtil.generateStringByArguments(this.message, arguments))
                .notificationLink(NotificationConvertUtil.generateNotificationLinkByArguments(this.notificationLinkType, arguments))
                .build();
    }
}
