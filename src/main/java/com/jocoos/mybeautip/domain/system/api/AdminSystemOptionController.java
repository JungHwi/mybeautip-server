package com.jocoos.mybeautip.domain.system.api;

import com.jocoos.mybeautip.domain.system.code.SystemOptionType;
import com.jocoos.mybeautip.domain.system.dto.SystemOptionRequest;
import com.jocoos.mybeautip.domain.system.dto.SystemOptionResponse;
import com.jocoos.mybeautip.domain.system.service.SystemOptionService;
import com.jocoos.mybeautip.global.dto.single.BooleanDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/system")
public class AdminSystemOptionController {

    private final SystemOptionService service;

    @PatchMapping("/{id}")
    public ResponseEntity<SystemOptionResponse> update(@PathVariable SystemOptionType id,
                                                       @RequestBody BooleanDto value) {
        SystemOptionRequest request = new SystemOptionRequest(id, value.isBool());
        SystemOptionResponse result = service.update(request);

        return ResponseEntity.ok(result);
    }
}
