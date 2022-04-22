package com.jocoos.mybeautip.domain.notification.vo;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class NotificationInfo {
    NotificationMessageInfo messageInfo;
    List<NotificationTargetInfo> targetInfoList;
}
