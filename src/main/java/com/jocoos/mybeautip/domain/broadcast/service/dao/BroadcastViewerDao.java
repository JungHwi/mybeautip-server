package com.jocoos.mybeautip.domain.broadcast.service.dao;

import com.jocoos.mybeautip.domain.broadcast.code.BroadcastViewerType;
import com.jocoos.mybeautip.domain.broadcast.dto.GrantManagerRequest;
import com.jocoos.mybeautip.domain.broadcast.dto.ViewerSuspendRequest;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastViewer;
import com.jocoos.mybeautip.domain.broadcast.persistence.repository.BroadcastViewerRepository;
import com.jocoos.mybeautip.domain.broadcast.vo.ViewerSearchCondition;
import com.jocoos.mybeautip.domain.broadcast.vo.ViewerSearchResult;
import com.jocoos.mybeautip.global.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BroadcastViewerDao {

    private final BroadcastViewerRepository repository;

    @Transactional(readOnly = true)
    public Optional<BroadcastViewer> findBroadcastViewer(Long broadcastId, Long memberId) {
        return repository.findByBroadcastIdAndMemberId(broadcastId, memberId);
    }

    @Transactional(readOnly = true)
    public BroadcastViewer getBroadcastViewer(long broadcastId, long memberId) {
        return findBroadcastViewer(broadcastId, memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
    }

    @Transactional(readOnly = true)
    public boolean isManager(long broadcastId, long memberId) {
        return repository.existsByBroadcastIdAndMemberIdAndType(broadcastId, memberId, BroadcastViewerType.MANAGER);
    }

    @Transactional(readOnly = true)
    public List<ViewerSearchResult> search(ViewerSearchCondition condition) {
        return repository.search(condition);
    }

    @Transactional(readOnly = true)
    public List<BroadcastViewer> getViewer(Broadcast broadcast) {
        return repository.findByBroadcast(broadcast);
    }

    @Transactional
    public BroadcastViewer grantManager(GrantManagerRequest request) {
        BroadcastViewer viewer = getBroadcastViewer(request.broadcastId(), request.memberId());

        return viewer.grantManager(request.isManager());
    }

    @Transactional
    public BroadcastViewer suspend(ViewerSuspendRequest request) {
        BroadcastViewer viewer = getBroadcastViewer(request.broadcastId(), request.memberId());

        return viewer.suspend(request.isSuspended());
    }

    @Transactional
    public BroadcastViewer exile(long broadcastId, long memberId) {
        BroadcastViewer viewer = getBroadcastViewer(broadcastId, memberId);

        return viewer.exile();
    }

    @Transactional
    public List<BroadcastViewer> saveAll(List<BroadcastViewer> viewerList) {
        return repository.saveAll(viewerList);
    }
}
