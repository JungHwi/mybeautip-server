package com.jocoos.mybeautip.domain.notification.aspect.service;

import com.jocoos.mybeautip.domain.notification.code.TemplateType;
import com.jocoos.mybeautip.domain.notification.service.NotificationService;

public interface AspectNotificationService<T> extends NotificationService<T> {
    void occurs(Object object);
    TemplateType getTemplateType();
}
