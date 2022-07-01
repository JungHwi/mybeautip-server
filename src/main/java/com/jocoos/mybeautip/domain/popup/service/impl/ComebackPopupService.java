package com.jocoos.mybeautip.domain.popup.service.impl;

import com.jocoos.mybeautip.domain.event.code.EventStatus;
import com.jocoos.mybeautip.domain.event.code.EventType;
import com.jocoos.mybeautip.domain.event.persistence.domain.Event;
import com.jocoos.mybeautip.domain.event.persistence.domain.EventJoin;
import com.jocoos.mybeautip.domain.event.persistence.repository.EventJoinRepository;
import com.jocoos.mybeautip.domain.event.persistence.repository.EventRepository;
import com.jocoos.mybeautip.domain.popup.service.PopupTypeService;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.support.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class ComebackPopupService implements PopupTypeService {

    private final EventRepository eventRepository;
    private final EventJoinRepository eventJoinRepository;

    @Override
    public boolean isPopup(Member member) {
        if (member == null) {
            return false;
        }

        Event event = eventRepository.findTopByTypeAndStatus(EventType.SIGNUP, EventStatus.PROGRESS);
        if (event == null) {
            return false;
        }

        EventJoin eventJoin = eventJoinRepository.findTopByMemberIdAndEventId(member.getId(), event.getId());

        if (eventJoin == null) {
            return false;
        }

        LocalDate signupDate = DateUtils.toLocalDate(member.getCreatedAt());
        LocalDate eventJoinDate = DateUtils.toLocalDate(eventJoin.getCreatedAt());

        return !signupDate.isEqual(eventJoinDate);
    }
}
