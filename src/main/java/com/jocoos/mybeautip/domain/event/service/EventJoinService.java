package com.jocoos.mybeautip.domain.event.service;

import com.jocoos.mybeautip.domain.event.converter.EventJoinConverter;
import com.jocoos.mybeautip.domain.event.dto.EventJoinHistoryResponse;
import com.jocoos.mybeautip.domain.event.dto.EventJoinResponse;
import com.jocoos.mybeautip.domain.event.persistence.domain.Event;
import com.jocoos.mybeautip.domain.event.persistence.domain.EventJoin;
import com.jocoos.mybeautip.domain.event.persistence.repository.EventJoinRepository;
import com.jocoos.mybeautip.domain.event.persistence.repository.EventRepository;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jocoos.mybeautip.global.constant.ErrorCodeConstant.EVENT_NOT_FOUND;
import static com.jocoos.mybeautip.global.constant.ErrorCodeConstant.MEMBER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class EventJoinService {

    private final EventTypeFactory eventTypeFactory;

    private final EventRepository eventRepository;
    private final EventJoinRepository repository;
    private final MemberRepository memberRepository;

    private final EventJoinConverter converter;


    @Transactional
    public EventJoinResponse join(long eventId, long memberId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(EVENT_NOT_FOUND, "Not found event - " + eventId));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(MEMBER_NOT_FOUND, "No such member. id - " + memberId));

        EventTypeService eventTypeService = eventTypeFactory.getEventTypeService(event.getType());

        EventJoin eventJoin = eventTypeService.join(event, member);
        return converter.convertsToJoin(eventJoin);
    }

    public List<EventJoinHistoryResponse> selectEventJoinHistory(long memberId, long cursor, int size) {
        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"));
        Slice<EventJoin> eventJoinSlice = repository.findByMemberIdAndIdLessThan(memberId, cursor, pageable);

        List<EventJoinHistoryResponse> result = converter.convertsToResponse(eventJoinSlice.getContent());

        return result;
    }
}
