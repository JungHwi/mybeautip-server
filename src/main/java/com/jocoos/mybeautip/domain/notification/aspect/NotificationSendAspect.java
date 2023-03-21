package com.jocoos.mybeautip.domain.notification.aspect;


import com.jocoos.mybeautip.domain.notification.aspect.annotation.SendNotification;
import com.jocoos.mybeautip.domain.notification.aspect.service.AspectNotificationService;
import com.jocoos.mybeautip.domain.notification.aspect.service.AspectNotificationServiceFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Log4j2
@Component
@Aspect
public class NotificationSendAspect {

    private final AspectNotificationServiceFactory aspectNotificationServiceFactory;

    // 알림 메세지 형식이 다르기 때문에 Factory 통해 타입에 따른 메세지 형식을 가져와 전송
    @AfterReturning(value = "@annotation(sendNotification)", returning = "result")
    public void sendNotification(JoinPoint joinPoint, SendNotification sendNotification, Object result) {
        log.debug("joinPoint: {}", joinPoint.toLongString());
        var services = aspectNotificationServiceFactory.get(sendNotification.templateTypes());
        if (result instanceof List<?> results) {
            sendAll(services, results);
        } else {
            send(services, result);
        }
    }

    private void sendAll(List<AspectNotificationService<Object>> services, List<?> results) {
        results.forEach(result -> send(services, result));
    }

    private void send(List<AspectNotificationService<Object>> services, Object result) {
        services.forEach(service -> service.occurs(result));
    }
}
