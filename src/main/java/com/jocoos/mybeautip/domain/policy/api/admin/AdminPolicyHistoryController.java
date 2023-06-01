package com.jocoos.mybeautip.domain.policy.api.admin;

import com.jocoos.mybeautip.domain.delivery.dto.DeliveryFeePolicySearchRequest;
import com.jocoos.mybeautip.domain.policy.dto.PolicyHistoryListResponse;
import com.jocoos.mybeautip.domain.policy.service.PolicyHistoryService;
import com.jocoos.mybeautip.global.wrapper.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/policy/history")
public class AdminPolicyHistoryController {

    private final PolicyHistoryService service;

    @GetMapping
    public PageResponse<PolicyHistoryListResponse> search(@RequestParam(required = false, defaultValue = "1") int page,
                                                          @RequestParam(required = false, defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "id"));

        return service.search(pageable);
    }
}
