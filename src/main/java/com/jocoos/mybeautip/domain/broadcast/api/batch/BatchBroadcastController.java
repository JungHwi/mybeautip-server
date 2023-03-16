package com.jocoos.mybeautip.domain.broadcast.api.batch;

import com.jocoos.mybeautip.domain.broadcast.service.BroadcastViewerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastBatchUpdateStatusResponse;
import com.jocoos.mybeautip.domain.broadcast.service.BatchBroadcastService;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/batch")
public class BatchBroadcastController {

    private final BatchBroadcastService service;
    private final BroadcastViewerService broadcastViewerService;

    @PutMapping("/1/broadcast/viewer/sync")
    public void synchronizeMemberList() {

        broadcastViewerService.syncViewer();
    }

    @PatchMapping("/broadcast/status")
    public BroadcastBatchUpdateStatusResponse bulkChangeStatus() {
        return service.bulkChangeStatus();
    }
}
