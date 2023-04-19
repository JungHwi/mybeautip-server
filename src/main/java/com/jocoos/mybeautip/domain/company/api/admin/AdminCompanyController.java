package com.jocoos.mybeautip.domain.company.api.admin;

import com.jocoos.mybeautip.domain.company.dto.CompanyResponse;
import com.jocoos.mybeautip.domain.company.dto.CreateCompanyRequest;
import com.jocoos.mybeautip.domain.company.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminCompanyController {

    private final CompanyService service;

    @PostMapping("/company")
    public CompanyResponse create(@RequestBody CreateCompanyRequest request) {

        return service.create(request);
    }
}
