package com.jocoos.mybeautip.domain.event.aspect;

import com.jocoos.mybeautip.domain.event.code.EventType;
import com.jocoos.mybeautip.domain.event.persistence.domain.Event;
import com.jocoos.mybeautip.domain.event.service.EventService;
import com.jocoos.mybeautip.domain.event.service.EventTypeFactory;
import com.jocoos.mybeautip.domain.event.service.EventTypeService;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
@RequiredArgsConstructor
public class EventAspect {

    private final EventService eventService;
    private final EventTypeFactory eventTypeFactory;

    @AfterReturning(value = "execution(* com.jocoos.mybeautip.member.LegacyMemberService.register(..))", returning = "result")
    public void join(JoinPoint joinPoint, Object result) {
        Member member = null;
        if (result instanceof Member) {
            member = (Member) result;
        }

        List<Event> signupEventList =  eventService.getProgressEventByType(EventType.SIGNUP);
        for (Event event : signupEventList) {
            EventTypeService eventTypeService = eventTypeFactory.getEventTypeService(event.getType());
            eventTypeService.join(event, member);
        }
    }
}
