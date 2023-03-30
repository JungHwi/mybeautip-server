package com.jocoos.mybeautip.domain.broadcast.api.batch;

import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastBatchUpdateStatusResponse;
import com.jocoos.mybeautip.domain.broadcast.service.BatchBroadcastService;
import com.jocoos.mybeautip.domain.broadcast.service.BroadcastViewerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/batch")
public class BatchBroadcastController {

    private final BatchBroadcastService service;
    private final BroadcastViewerService broadcastViewerService;

    @PutMapping("/broadcast/viewer/sync")
    public void synchronizeMemberList() {

        broadcastViewerService.syncViewer();
    }

    @PatchMapping("/broadcast/status")
    public List<BroadcastBatchUpdateStatusResponse> bulkChangeStatus() {
        List<BroadcastBatchUpdateStatusResponse> response = service.bulkChangeStatus();
        log.debug("bulk change status result : {}", response);
        return response;
    }
}
