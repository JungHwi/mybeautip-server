package com.jocoos.mybeautip.domain.event.api.batch;

import com.jocoos.mybeautip.domain.event.dto.EventBatchResult;
import com.jocoos.mybeautip.domain.event.service.AdminEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/batch")
public class BatchEventController {

    private final AdminEventService adminEventService;

    @PatchMapping("/event/status")
    public ResponseEntity<EventBatchResult> batchStatus() {
        EventBatchResult result = adminEventService.batchStatus();
        return ResponseEntity.ok(result);
    }
}
