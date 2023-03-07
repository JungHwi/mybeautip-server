package com.jocoos.mybeautip.domain.broadcast.service.dao;

import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.persistence.repository.BroadcastRepository;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastSearchCondition;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastSearchResult;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

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
        return repository.findById(broadcastId)
                .orElseThrow(() -> new NotFoundException("Not found broadcast. id - " + broadcastId));
    }

    @Transactional(readOnly = true)
    public BroadcastSearchResult getWithMemberAndCategory(long broadcastId) {
        return repository.get(broadcastId);
    }

    @Transactional(readOnly = true)
    public boolean isCreator(long broadcastId, long memberId) {
        return repository.existsByIdAndMemberId(broadcastId, memberId);
    }

    @Transactional(readOnly = true)
    public List<BroadcastSearchResult> getList(BroadcastSearchCondition condition) {
        return repository.getList(condition);
    }

    @Transactional(readOnly = true)
    public Slice<ZonedDateTime> getStartedAtList() {
        return repository.findAllStartedAt();
    }

    @Transactional
    public void addReportCountAndFlush(long broadcastId, int count) {
        repository.addReportCountAndFlush(broadcastId, count);
    }
}
