package com.jocoos.mybeautip.domain.company.api.admin;

import com.jocoos.mybeautip.domain.company.code.CompanyStatus;
import com.jocoos.mybeautip.domain.company.dto.*;
import com.jocoos.mybeautip.domain.company.service.CompanyService;
import com.jocoos.mybeautip.global.wrapper.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminCompanyController {

    private final CompanyService service;

    @PostMapping("/company")
    public CompanyResponse create(@RequestBody CreateCompanyRequest request) {

        return service.create(request);
    }

    @GetMapping("/company")
    public PageResponse<CompanyListResponse> search(@RequestParam(required = false) String name,
                                                    @RequestParam(required = false) Set<CompanyStatus> status,
                                                    @RequestParam(required = false, defaultValue = "1") int page,
                                                    @RequestParam(required = false, defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "id"));

        CompanySearchRequest request = CompanySearchRequest.builder()
                .name(name)
                .status(status)
                .pageable(pageable)
                .build();

        return service.search(request);
    }

    @GetMapping("/company/{companyId}")
    public CompanyResponse get(@PathVariable long companyId) {

        return service.get(companyId);
    }

    @PutMapping("/company/{companyId}")
    public CompanyResponse edit(@PathVariable long companyId,
                                @RequestBody EditCompanyRequest request) {

        return service.edit(companyId, request);
    }
}
