package com.jocoos.mybeautip.domain.broadcast.service;

import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus;
import com.jocoos.mybeautip.domain.broadcast.converter.BroadcastConverter;
import com.jocoos.mybeautip.domain.broadcast.dto.AdminBroadcastResponse;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastDao;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastSearchCondition;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastSearchResult;
import com.jocoos.mybeautip.global.vo.SearchOption;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus.getSearchStatuses;

@RequiredArgsConstructor
@Service
public class AdminBroadcastService {

    private final BroadcastDao broadcastDao;
    private final BroadcastConverter converter;

    @Transactional(readOnly = true)
    public List<AdminBroadcastResponse> getList(BroadcastStatus status, SearchOption searchOption, Pageable pageable) {
        BroadcastSearchCondition condition = BroadcastSearchCondition.builder()
                .statuses(getStatuses(status))
                .pageable(pageable)
                .searchOption(searchOption)
                .build();
        List<BroadcastSearchResult> searchResults = broadcastDao.getList(condition);
        return converter.toAdminResponse(searchResults);
    }

    private List<BroadcastStatus> getStatuses(BroadcastStatus status) {
        return status == null ? null : getSearchStatuses(status);
    }
}
