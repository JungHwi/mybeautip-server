package com.jocoos.mybeautip.domain.event.service;

import com.jocoos.mybeautip.domain.event.converter.EventJoinConverter;
import com.jocoos.mybeautip.domain.event.dto.EventJoinHistoryResponse;
import com.jocoos.mybeautip.domain.event.persistence.domain.EventJoin;
import com.jocoos.mybeautip.domain.event.persistence.repository.EventJoinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventJoinService {

    private final EventJoinRepository repository;
    private final EventJoinConverter converter;

    public List<EventJoinHistoryResponse> selectEventJoinHistory(long memberId, long cursor, int size) {
        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"));
        Slice<EventJoin> eventJoinSlice = repository.findByMemberIdAndIdLessThan(memberId, cursor, pageable);

        List<EventJoinHistoryResponse> result = converter.convertsToResponse(eventJoinSlice.getContent());

        return result;
    }
}
