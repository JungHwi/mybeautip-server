package com.jocoos.mybeautip.domain.policy.service;

import com.jocoos.mybeautip.domain.policy.converter.PolicyHistoryConverter;
import com.jocoos.mybeautip.domain.policy.dto.PolicyHistoryListResponse;
import com.jocoos.mybeautip.domain.policy.persistence.domain.PolicyHistory;
import com.jocoos.mybeautip.domain.policy.service.dao.PolicyHistoryDao;
import com.jocoos.mybeautip.global.wrapper.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PolicyHistoryService {

    private final PolicyHistoryDao dao;
    private final PolicyHistoryConverter converter;

    public PageResponse<PolicyHistoryListResponse> search(Pageable pageable) {
        Page<PolicyHistory> policyHistoryPages = dao.search(pageable);
        List<PolicyHistoryListResponse> policyHistoryList = converter.converts(policyHistoryPages.getContent());

        return new PageResponse<>(policyHistoryPages.getTotalElements(), policyHistoryList);
    }
}
