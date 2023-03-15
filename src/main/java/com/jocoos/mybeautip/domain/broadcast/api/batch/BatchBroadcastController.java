package com.jocoos.mybeautip.domain.broadcast.api.batch;

import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastBatchUpdateStatusResponse;
import com.jocoos.mybeautip.domain.broadcast.service.BatchBroadcastService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/batch")
@RestController
public class BatchBroadcastController {

    private final BatchBroadcastService service;

    @PatchMapping("/broadcast/status")
    public BroadcastBatchUpdateStatusResponse bulkChangeStatus() {
        return service.bulkChangeStatus();
    }
}
