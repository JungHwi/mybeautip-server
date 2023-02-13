package com.jocoos.mybeautip.domain.broadcast.service.dao;

import com.jocoos.mybeautip.domain.broadcast.code.BroadcastViewerType;
import com.jocoos.mybeautip.domain.broadcast.persistence.repository.BroadcastViewerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BroadcastViewerDao {

    private final BroadcastViewerRepository repository;

    @Transactional(readOnly = true)
    public boolean isManager(long broadcastId, long memberId) {
        return repository.existsByBroadcastIdAndMemberIdAndType(broadcastId, memberId, BroadcastViewerType.MANAGER);
    }
}
