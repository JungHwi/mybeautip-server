package com.jocoos.mybeautip.domain.notification.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jocoos.mybeautip.domain.notification.code.MessageType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.LOCAL_DATE_TIME_FORMAT;

@Getter
@Builder
public class CenterMessageResponse {

    private long id;

    private MessageType messageType;

    private String message;

    private String imageUrl;

    private String deepLink;

    @JsonFormat(pattern = LOCAL_DATE_TIME_FORMAT)
    private LocalDateTime createdAt;
}
