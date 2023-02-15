package com.jocoos.mybeautip.domain.broadcast.api.admin;

import com.jocoos.mybeautip.domain.broadcast.code.BroadcastViewerType;
import com.jocoos.mybeautip.domain.broadcast.dto.ViewerResponse;
import com.jocoos.mybeautip.domain.broadcast.service.BroadcastViewerService;
import com.jocoos.mybeautip.domain.broadcast.vo.ViewerSearchCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminBroadcastViewerController {

    private final BroadcastViewerService viewerService;

    @GetMapping("/broadcast/{broadcast_id}/viewer")
    public ResponseEntity<List<ViewerResponse>> search(@PathVariable("broadcast_id") long broadcastId,
                                                       @RequestParam(name = "type", required = false)BroadcastViewerType type,
                                                       @RequestParam(name = "cursor", required = false) Long cursor,
                                                       @RequestParam(required = false, defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.ASC, "sortedUsername"));

        ViewerSearchCondition condition = ViewerSearchCondition.builder()
                .broadcastId(broadcastId)
                .type(type)
                .cursor(cursor)
                .pageable(pageable)
                .build();

        List<ViewerResponse> responses = viewerService.search(condition);

        return ResponseEntity.ok(responses);
    }
}
