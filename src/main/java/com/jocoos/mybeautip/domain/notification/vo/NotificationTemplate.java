package com.jocoos.mybeautip.domain.notification.vo;

import com.jocoos.mybeautip.domain.notification.code.NotificationArgument;
import com.jocoos.mybeautip.domain.notification.code.SendType;
import com.jocoos.mybeautip.domain.notification.code.TemplateType;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
public class NotificationTemplate {
    private TemplateType id;

    private String description;

    private Set<SendType> sendTypes;

    private Set<NotificationArgument> availableArguments;
}
