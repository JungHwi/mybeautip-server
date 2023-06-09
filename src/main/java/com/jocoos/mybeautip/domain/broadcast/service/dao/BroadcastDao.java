package com.jocoos.mybeautip.domain.broadcast.service.dao;

import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.persistence.repository.BroadcastRepository;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastSearchCondition;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastSearchResult;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastUpdateCandidate;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastUpdateCandidateCondition;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import static com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus.*;
import static com.jocoos.mybeautip.global.exception.ErrorCode.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class BroadcastDao {

    private final BroadcastRepository repository;

    @Transactional(readOnly = true)
    public int countByMemberId(long memberId) {
        return repository.countByMemberId(memberId);
    }

    @Transactional
    public Broadcast save(Broadcast broadcast) {
        return repository.save(broadcast);
    }

    @Transactional(readOnly = true)
    public Broadcast get(long broadcastId) {
        return repository.findByIdWithFetch(broadcastId)
                .orElseThrow(broadcastNotFoundException(broadcastId));
    }

    @Transactional(readOnly = true)
    public BroadcastSearchResult getSearchResult(long broadcastId) {
        return repository.get(broadcastId)
                .orElseThrow(broadcastNotFoundException(broadcastId));
    }

    @Transactional(readOnly = true)
    public List<Broadcast> getAllByIdIn(List<Long> ids) {
        return repository.findAllByIdIn(ids);
    }

    @Transactional(readOnly = true)
    public boolean isCreator(long broadcastId, long memberId) {
        return repository.existsByIdAndMemberId(broadcastId, memberId);
    }

    @Transactional(readOnly = true)
    public Optional<Broadcast> findLiveOrReadyNowBroadcast(Long memberId) {
        return repository.findByStatusInAndMemberId(List.of(LIVE, READY), memberId);
    }

    @Transactional(readOnly = true)
    public boolean isCreatorHaveLiveIn(Long memberId, ZonedDateTime startedAt) {
        return repository.existsByMemberIdAndStartedAtAndStatusIn(memberId, startedAt, ACTIVE_STATUSES);
    }

    @Transactional(readOnly = true)
    public List<BroadcastSearchResult> getList(BroadcastSearchCondition condition) {
        return repository.getList(condition);
    }

    @Transactional(readOnly = true)
    public List<Broadcast> findByStatusIn(List<BroadcastStatus> statusList) {
        return repository.findByStatusIn(statusList);
    }

    @Transactional(readOnly = true)
    public Page<BroadcastSearchResult> getPageList(BroadcastSearchCondition condition) {
        return repository.getPage(condition);
    }

    @Transactional(readOnly = true)
    public Slice<ZonedDateTime> getStartedAtList(List<BroadcastStatus> statuses, Pageable pageable) {
        return repository.findAllStartedAt(statuses, pageable);
    }

    @Transactional(readOnly = true)
    public Broadcast getByVideoKey(long videoKey) {
        return repository.findByVideoKey(videoKey)
                .orElseThrow(() -> new NotFoundException("VideoKey Not Found. videoKey - " + videoKey));
    }

    @Transactional(readOnly = true)
    public long count(BroadcastSearchCondition condition) {
        return repository.count(condition);
    }

    @Transactional
    public void addReportCountAndFlush(long broadcastId, int count) {
        repository.addReportCountAndFlush(broadcastId, count);
    }

    @Transactional
    public void addHeartCountAndFlush(Long broadcastId, int count) {
        repository.addHeartCountAndFlush(broadcastId, count);
    }

    private Supplier<NotFoundException> broadcastNotFoundException(long broadcastId) {
        return () -> new NotFoundException(NOT_FOUND, "Not found broadcast. id - " + broadcastId);
    }

    @Transactional
    public void updatePausedAt(Long videoKey, ZonedDateTime pausedAt) {
        repository.updatePausedAt(videoKey, pausedAt);
    }

    @Transactional
    public void bulkUpdateToEnd(List<Long> ids) {
        repository.bulkUpdateStatusAndEndedAt(ids, END, ZonedDateTime.now());
    }

    @Transactional
    public void bulkUpdateToCancel(List<Long> ids) {
        repository.bulkUpdateStatusAndEndedAt(ids, CANCEL, ZonedDateTime.now());
    }

    @Transactional
    public void bulkUpdateToReady(List<Long> ids) {
        repository.bulkUpdateStatus(ids, READY);
    }

    @Transactional(readOnly = true)
    public List<BroadcastUpdateCandidate> getCandidates(BroadcastUpdateCandidateCondition condition) {
        return repository.getUpdateCandidates(condition);
    }

    @Transactional(readOnly = true)
    public Set<Long> getCreatorIdsLiveNowIn(Set<Long> memberIds) {
        return repository.getCreatorIdInAndStatus(memberIds, LIVE);
    }

    @Transactional(readOnly = true)
    public List<Broadcast> getAllByCreatorIdIn(List<Long> memberIds, List<BroadcastStatus> statuses) {
        return repository.findAllByMemberIdInAndStatusIn(memberIds, statuses);
    }

    @Transactional(readOnly = true)
    public List<Broadcast> getByMemberIdAndStatus(Long memberId, BroadcastStatus status) {
        return repository.findByMemberIdAndStatus(memberId, status);
    }
}
