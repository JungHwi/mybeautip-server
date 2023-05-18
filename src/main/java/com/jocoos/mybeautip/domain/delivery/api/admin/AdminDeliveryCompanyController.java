package com.jocoos.mybeautip.domain.delivery.api.admin;

import com.jocoos.mybeautip.domain.delivery.dto.CreateDeliveryCompanyRequest;
import com.jocoos.mybeautip.domain.delivery.dto.DeliveryCompanyResponse;
import com.jocoos.mybeautip.domain.delivery.service.DeliveryCompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/delivery/company")
public class AdminDeliveryCompanyController {

    private final DeliveryCompanyService service;

    @PostMapping
    public DeliveryCompanyResponse create(@RequestBody CreateDeliveryCompanyRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<DeliveryCompanyResponse> get() {
        return service.search();
    }
}
