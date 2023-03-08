package com.jocoos.mybeautip.domain.broadcast.service.dao;

import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.persistence.repository.BroadcastRepository;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastSearchCondition;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastSearchResult;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class BroadcastDao {

    private final BroadcastRepository repository;

    @Transactional
    public Broadcast save(Broadcast broadcast) {
        return repository.save(broadcast);
    }

    @Transactional(readOnly = true)
    public Broadcast get(long broadcastId) {
        return repository.findByIdWithCategory(broadcastId)
                .orElseThrow(broadcastNotFoundException(broadcastId));
    }

    @Transactional(readOnly = true)
    public BroadcastSearchResult getWithMemberAndCategory(long broadcastId) {
        return repository.get(broadcastId)
                .orElseThrow(broadcastNotFoundException(broadcastId));
    }

    @Transactional(readOnly = true)
    public boolean isCreator(long broadcastId, long memberId) {
        return repository.existsByIdAndMemberId(broadcastId, memberId);
    }

    @Transactional(readOnly = true)
    public Page<BroadcastSearchResult> getList(BroadcastSearchCondition condition) {
        return repository.getList(condition);
    }

    @Transactional(readOnly = true)
    public Slice<ZonedDateTime> getStartedAtList(Pageable pageable) {
        return repository.findAllStartedAt(pageable);
    }

    @Transactional
    public void addReportCountAndFlush(long broadcastId, int count) {
        repository.addReportCountAndFlush(broadcastId, count);
    }

    private Supplier<NotFoundException> broadcastNotFoundException(long broadcastId) {
        return () -> new NotFoundException("Not found broadcast. id - " + broadcastId);
    }
}
