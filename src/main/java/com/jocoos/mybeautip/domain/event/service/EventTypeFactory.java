package com.jocoos.mybeautip.domain.event.service;

import com.jocoos.mybeautip.domain.event.code.EventType;
import com.jocoos.mybeautip.domain.event.service.impl.*;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventTypeFactory {

    private final SignupEventService signupEventService;
    private final InviteEventService inviteEventService;
    private final RouletteEventService rouletteEventService;
    private final JoinEventService joinEventService;
    private final DripEventService dripEventService;

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
            case DRIP:
                return dripEventService;
            default:
                throw new BadRequestException("not_support_event", "Not support event. type is " + eventType);
        }
    }
}