package com.jocoos.mybeautip.domain.delivery.api.admin;

import com.jocoos.mybeautip.domain.delivery.code.DeliveryFeeSearchField;
import com.jocoos.mybeautip.domain.delivery.code.DeliveryFeeType;
import com.jocoos.mybeautip.domain.delivery.dto.*;
import com.jocoos.mybeautip.domain.delivery.service.DeliveryFeePolicyService;
import com.jocoos.mybeautip.global.wrapper.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/delivery/fee")
public class AdminDeliveryFeePolicyController {

    private final DeliveryFeePolicyService service;

    @PostMapping
    public DeliveryFeePolicyResponse create(@RequestBody CreateDeliveryFeePolicyRequest request) {

        return service.create(request);
    }

    @GetMapping
    public PageResponse<DeliveryFeePolicyListResponse> search(@RequestParam(required = false, name = "search_field") DeliveryFeeSearchField searchField,
                                                              @RequestParam(required = false, name = "search_text") String searchText,
                                                              @RequestParam(required = false) DeliveryFeeType type,
                                                              @RequestParam(required = false, defaultValue = "1") int page,
                                                              @RequestParam(required = false, defaultValue = "10") int size ) {

        Pageable pageable = PageRequest.of(page - 1, size);
        DeliveryFeePolicySearchRequest request = new DeliveryFeePolicySearchRequest(searchField, searchText, type, pageable);
        return service.search(request);
    }

    @PutMapping("/{deliveryFeeId}")
    public DeliveryFeePolicyResponse edit(@PathVariable long deliveryFeeId,
                                          @RequestBody EditDeliveryFeePolicyRequest request) {

        return service.edit(deliveryFeeId, request);
    }
}
