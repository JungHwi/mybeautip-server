package com.jocoos.mybeautip.domain.broadcast.service.dao;

import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.persistence.repository.BroadcastRepository;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BroadcastDao {

    private final BroadcastRepository repository;

    @Transactional(readOnly = true)
    public Broadcast get(long broadcastId) {
        return repository.findById(broadcastId)
                .orElseThrow(() -> new NotFoundException("Not found broadcast. id - " + broadcastId));
    }

    @Transactional(readOnly = true)
    public boolean isCreator(long broadcastId, long memberId) {
        return repository.existsByIdAndMemberId(broadcastId, memberId);
    }
}
