package com.jocoos.mybeautip.domain.event.service;

import com.jocoos.mybeautip.domain.event.code.EventType;
import com.jocoos.mybeautip.domain.event.service.impl.InviteEventService;
import com.jocoos.mybeautip.domain.event.service.impl.JoinEventService;
import com.jocoos.mybeautip.domain.event.service.impl.RouletteEventServiceType;
import com.jocoos.mybeautip.domain.event.service.impl.SignupEventService;
import com.jocoos.mybeautip.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventTypeFactory {

    private final SignupEventService signupEventService;
    private final InviteEventService inviteEventService;
    private final RouletteEventServiceType rouletteEventService;
    private final JoinEventService joinEventService;

    public EventTypeService getEventTypeService(EventType eventType) {
        switch (eventType) {
            case SIGNUP:
                return signupEventService;
            case INVITE:
                return inviteEventService;
            case ROULETTE:
                return rouletteEventService;
            case JOIN:
                return joinEventService;
            default:
                throw new BadRequestException("not_support_event", "Not support event. type is " + eventType);
        }
    }
}