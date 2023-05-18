package com.jocoos.mybeautip.domain.delivery.api.admin;

import com.jocoos.mybeautip.domain.delivery.dto.CreateDeliveryCompanyRequest;
import com.jocoos.mybeautip.domain.delivery.dto.DeliveryCompanyResponse;
import com.jocoos.mybeautip.domain.delivery.dto.EditDeliveryCompanyRequest;
import com.jocoos.mybeautip.domain.delivery.service.DeliveryCompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PutMapping("/{deliveryCompanyId}")
    public DeliveryCompanyResponse edit(@PathVariable long deliveryCompanyId,
                                        @RequestBody EditDeliveryCompanyRequest request) {

        return service.edit(deliveryCompanyId, request);
    }

    @DeleteMapping("/{deliveryCompanyId}")
    public ResponseEntity delete(@PathVariable long deliveryCompanyId) {
        service.delete(deliveryCompanyId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
