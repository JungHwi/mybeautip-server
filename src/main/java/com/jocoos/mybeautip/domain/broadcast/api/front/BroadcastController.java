package com.jocoos.mybeautip.domain.broadcast.api.front;

import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastListResponse;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastRequest;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastResponse;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastStartedAtResponse;
import com.jocoos.mybeautip.domain.broadcast.service.BroadcastService;
import com.jocoos.mybeautip.domain.community.dto.ReportRequest;
import com.jocoos.mybeautip.global.annotation.CheckPermission;
import com.jocoos.mybeautip.global.annotation.CurrentMember;
import com.jocoos.mybeautip.global.wrapper.CursorResultResponse;
import com.jocoos.mybeautip.security.MyBeautipUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static com.jocoos.mybeautip.global.code.PermissionType.INFLUENCER;

@RequiredArgsConstructor
@RequestMapping("/api/")
@RestController
public class BroadcastController {

    private final BroadcastService service;

    @CheckPermission({INFLUENCER})
    @PostMapping("/1/broadcast")
    public BroadcastListResponse create(@RequestBody BroadcastRequest request) {
        return service.createBroadcastAndVod(request);
    }

    @GetMapping("/1/broadcast")
    public CursorResultResponse<BroadcastListResponse> getList(@RequestParam(required = false) BroadcastStatus status,
                                                               @RequestParam(required = false, name = "start_at") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startAt,
                                                               @RequestParam(required = false) Long cursor,
                                                               @RequestParam(required = false, defaultValue = "10") int size) {
        List<BroadcastListResponse> responses = service.getList(status, startAt, cursor, size);
        return new CursorResultResponse<>(responses);
    }

    @GetMapping("/1/broadcast/startedAt")
    public BroadcastStartedAtResponse getDateList() {
        return service.getDateList();
    }

    @GetMapping("/1/broadcast/{broadcastId}")
    public BroadcastResponse get(@PathVariable long broadcastId) {
        return service.get(broadcastId);
    }

    @PostMapping("/1/broadcast/{broadcastId}/report")
    public void report(@PathVariable long broadcastId,
                                      @CurrentMember MyBeautipUserDetails userDetails,
                                      @RequestBody ReportRequest request) {
        service.report(broadcastId, userDetails.getMember().getId(), request.getDescription());
    }

}
