package com.jocoos.mybeautip.domain.notification.aspect.service;

import com.jocoos.mybeautip.domain.notification.code.TemplateType;
import com.jocoos.mybeautip.domain.notification.service.NotificationService;

public interface AspectNotificationService<T> extends NotificationService<T> {

    TemplateType getTemplateType();
    void occurs(Object object);
}
