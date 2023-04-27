package com.jocoos.mybeautip.domain.brand.api.admin;

import com.jocoos.mybeautip.domain.brand.code.BrandStatus;
import com.jocoos.mybeautip.domain.brand.dto.BrandListResponse;
import com.jocoos.mybeautip.domain.brand.dto.BrandResponse;
import com.jocoos.mybeautip.domain.brand.dto.BrandSearchRequest;
import com.jocoos.mybeautip.domain.brand.dto.CreateBrandRequest;
import com.jocoos.mybeautip.domain.brand.service.BrandService;
import com.jocoos.mybeautip.global.wrapper.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/brand")
public class AdminBrandController {

    private final BrandService service;

    @PostMapping
    public BrandResponse create(@RequestBody CreateBrandRequest request) {
        return service.create(request);
    }

    @GetMapping
    public PageResponse<BrandListResponse> get(@RequestParam(required = false) String searchField,
                                               @RequestParam(required = false) String keyword,
                                               @RequestParam(required = false) BrandStatus status,
                                               @RequestParam(required = false, defaultValue = "id") String sort,
                                               @RequestParam(required = false, defaultValue = "DESC") Sort.Direction order,
                                               @RequestParam(required = false, defaultValue = "1") int page,
                                               @RequestParam(required = false, defaultValue = "10") int size) {

        PageRequest pageRequest = PageRequest.of(page - 1, size, order, sort);
        BrandSearchRequest request = BrandSearchRequest.builder()
                .searchField(searchField)
                .keyword(keyword)
                .status(status)
                .pageable(pageRequest)
                .build();

        return service.search(request);
    }
}
