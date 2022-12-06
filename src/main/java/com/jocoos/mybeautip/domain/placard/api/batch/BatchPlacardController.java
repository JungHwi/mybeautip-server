package com.jocoos.mybeautip.domain.placard.api.batch;

import com.jocoos.mybeautip.domain.placard.dto.BatchPlacardResult;
import com.jocoos.mybeautip.domain.placard.service.BatchPlacardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/batch")
@RestController
public class BatchPlacardController {

    private final BatchPlacardService service;

    @PatchMapping("/placard/status")
    public ResponseEntity<BatchPlacardResult> batchActiveAndInActivePlacard() {
        return ResponseEntity.ok(service.changeStatus());
    }
}
