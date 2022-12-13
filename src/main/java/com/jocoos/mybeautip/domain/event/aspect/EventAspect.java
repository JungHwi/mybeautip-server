package com.jocoos.mybeautip.domain.event.aspect;

import com.jocoos.mybeautip.domain.event.code.EventType;
import com.jocoos.mybeautip.domain.event.persistence.domain.Event;
import com.jocoos.mybeautip.domain.event.service.EventService;
import com.jocoos.mybeautip.domain.event.service.EventTypeFactory;
import com.jocoos.mybeautip.domain.event.service.EventTypeService;
import com.jocoos.mybeautip.domain.member.dto.MemberDetailResponse;
import com.jocoos.mybeautip.domain.member.vo.ChangedTagInfo;
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

    @AfterReturning(value = "execution(* com.jocoos.mybeautip.domain.member.service.MemberService.register(..))", returning = "result")
    public void signupEventAspect(JoinPoint joinPoint, Object result) {
        Member member = null;
        if (result instanceof Member) {
            member = (Member) result;
        }

        List<Event> signupEventList = eventService.getProgressEventByType(EventType.SIGNUP);
        EventTypeService eventTypeService = eventTypeFactory.getEventTypeService(EventType.SIGNUP);
        for (Event event : signupEventList) {
            eventTypeService.join(event, member);
        }
    }

    @AfterReturning(value = "execution(* com.jocoos.mybeautip.member.LegacyMemberService.updateDetailInfo(..))", returning = "result")
    public void inviteEventAspect(JoinPoint joinPoint, Object result) {
        MemberDetailResponse memberDetailResponse = null;
        if (result instanceof MemberDetailResponse) {
            memberDetailResponse = (MemberDetailResponse) result;
        } else {
            return;
        }

        ChangedTagInfo changedTagInfo = memberDetailResponse.getChangedTagInfo();

        if (!changedTagInfo.isChanged()) {
            return;
        }

        List<Event> inviteEventList = eventService.getProgressEventByType(EventType.INVITE);
        EventTypeService eventTypeService = eventTypeFactory.getEventTypeService(EventType.INVITE);
        for (Event event : inviteEventList) {
            eventTypeService.join(event, changedTagInfo.getMember());
            eventTypeService.join(event, changedTagInfo.getTargetMember());
        }
    }
}
