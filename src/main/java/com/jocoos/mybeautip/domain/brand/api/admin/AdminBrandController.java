package com.jocoos.mybeautip.domain.brand.api.admin;

import com.jocoos.mybeautip.domain.brand.dto.BrandResponse;
import com.jocoos.mybeautip.domain.brand.dto.CreateBrandRequest;
import com.jocoos.mybeautip.domain.brand.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminBrandController {

    private final BrandService service;

    @PostMapping("/brand")
    public BrandResponse create(@RequestBody CreateBrandRequest request) {
        return service.create(request);
    }
}
