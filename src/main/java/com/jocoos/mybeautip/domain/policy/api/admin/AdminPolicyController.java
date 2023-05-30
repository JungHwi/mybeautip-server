package com.jocoos.mybeautip.domain.policy.api.admin;

import com.jocoos.mybeautip.domain.policy.dto.EditPolicyRequest;
import com.jocoos.mybeautip.domain.policy.dto.PolicyResponse;
import com.jocoos.mybeautip.domain.policy.service.PolicyService;
import com.jocoos.mybeautip.global.code.CountryCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/policy")
public class AdminPolicyController {

    private final PolicyService service;

    @GetMapping("/{countryCode}")
    public PolicyResponse get(@PathVariable CountryCode countryCode) {
        return service.get(countryCode);
    }

    @PutMapping("/{countryCode}")
    public PolicyResponse edit(@PathVariable CountryCode countryCode,
                               @RequestBody EditPolicyRequest request) {

        return service.edit(countryCode, request);
    }
}
