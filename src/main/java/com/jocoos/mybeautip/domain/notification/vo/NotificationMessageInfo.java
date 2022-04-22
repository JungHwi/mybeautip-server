package com.jocoos.mybeautip.domain.notification.vo;

import com.jocoos.mybeautip.domain.notification.client.vo.AppPushMessage;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationMessageInfo {
    Long messageCenterId;
    AppPushMessage appPushMessage;
}
