package com.jocoos.mybeautip.domain.notification.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jocoos.mybeautip.domain.notification.code.MessageType;
import com.jocoos.mybeautip.domain.notification.code.NotificationStatus;
import com.jocoos.mybeautip.domain.notification.vo.NotificationLink;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.List;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;

@Getter
@Setter
public class CenterMessageResponse {

    private long id;

    private NotificationStatus status;

    private MessageType messageType;

    private String message;

    private String imageUrl;

    private List<NotificationLink> notificationLink;

    @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT)
    private ZonedDateTime createdAt;
}
