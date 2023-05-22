package com.jocoos.mybeautip.domain.delivery.api.admin;

import com.jocoos.mybeautip.domain.delivery.dto.CreateDeliveryFeePolicyRequest;
import com.jocoos.mybeautip.domain.delivery.dto.DeliveryFeePolicyResponse;
import com.jocoos.mybeautip.domain.delivery.service.DeliveryFeePolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/delivery/fee")
public class AdminDeliveryFeePolicyController {

    private final DeliveryFeePolicyService service;

    @PostMapping
    public DeliveryFeePolicyResponse create(@RequestBody CreateDeliveryFeePolicyRequest request) {

        return service.create(request);
    }
}
