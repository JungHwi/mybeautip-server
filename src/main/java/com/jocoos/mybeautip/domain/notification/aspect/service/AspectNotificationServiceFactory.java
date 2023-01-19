package com.jocoos.mybeautip.domain.notification.aspect.service;

import com.jocoos.mybeautip.domain.notification.code.TemplateType;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.toUnmodifiableMap;

@Component
public class AspectNotificationServiceFactory {

    private final Map<TemplateType, AspectNotificationService<?>> notificationServiceMap;

    public AspectNotificationServiceFactory(List<AspectNotificationService<?>> notificationServices) {
        notificationServiceMap = notificationServices.stream()
                .collect(toUnmodifiableMap(AspectNotificationService::getTemplateType, Function.identity()));
    }

    public <T> List<AspectNotificationService<T>> get(TemplateType[] templateTypes) {
        return Arrays.stream(templateTypes)
                .map(type -> (AspectNotificationService<T>) notificationServiceMap.get(type))
                .filter(Objects::nonNull)
                .toList();
    }
}
