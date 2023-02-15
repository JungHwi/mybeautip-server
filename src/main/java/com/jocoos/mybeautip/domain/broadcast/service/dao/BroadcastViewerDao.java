package com.jocoos.mybeautip.domain.broadcast.service.dao;

import com.jocoos.mybeautip.domain.broadcast.code.BroadcastViewerType;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastViewer;
import com.jocoos.mybeautip.domain.broadcast.persistence.repository.BroadcastViewerRepository;
import com.jocoos.mybeautip.domain.broadcast.vo.ViewerSearchCondition;
import com.jocoos.mybeautip.domain.broadcast.vo.ViewerSearchResult;
import com.jocoos.mybeautip.global.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BroadcastViewerDao {

    private final BroadcastViewerRepository repository;

    @Transactional(readOnly = true)
    public BroadcastViewer findByMemberId(long memberId) {
        return repository.findByMemberId(memberId)
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
}
