package com.jocoos.mybeautip.domain.broadcast.api.front;

import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus;
import com.jocoos.mybeautip.domain.broadcast.dto.*;
import com.jocoos.mybeautip.domain.broadcast.service.BroadcastService;
import com.jocoos.mybeautip.global.annotation.CurrentMember;
import com.jocoos.mybeautip.global.dto.IdAndBooleanResponse.NotificationResponse;
import com.jocoos.mybeautip.global.dto.IdAndCountResponse.ReportCountResponse;
import com.jocoos.mybeautip.global.dto.single.BooleanDto;
import com.jocoos.mybeautip.global.wrapper.CursorResultResponse;
import com.jocoos.mybeautip.security.MyBeautipUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.LOCAL_DATE_FORMAT;

@RequiredArgsConstructor
@RequestMapping("/api/")
@RestController
public class BroadcastController {

    private final BroadcastService service;

    @PostMapping("/1/broadcast")
    public BroadcastResponse create(@RequestBody @Valid BroadcastCreateRequest request,
                                    @CurrentMember MyBeautipUserDetails userDetails) {
        return service.createBroadcastAndVod(request, userDetails.getMember().getId());
    }

    @GetMapping("/1/broadcast")
    public CursorResultResponse<BroadcastListResponse> getList(@RequestParam(required = false) BroadcastStatus status,
                                                               @RequestParam(required = false, name = "start_date") @DateTimeFormat(pattern = LOCAL_DATE_FORMAT) LocalDate startDate,
                                                               @RequestParam(required = false) Long cursor,
                                                               @RequestParam(required = false, defaultValue = "10") int size) {
        List<BroadcastListResponse> responses = service.getList(status, startDate, cursor, size);
        return new CursorResultResponse<>(responses);
    }

    @GetMapping("/1/broadcast/{broadcastId}")
    public BroadcastResponse get(@PathVariable long broadcastId) {
        return service.get(broadcastId);
    }

    @PatchMapping("/1/broadcast/{broadcastId}")
    public BroadcastResponse edit(@PathVariable long broadcastId,
                                  @RequestBody @Valid BroadcastEditRequest request,
                                  @CurrentMember MyBeautipUserDetails userDetails) {
        return service.edit(broadcastId, request, userDetails.getMember().getId());
    }

    @GetMapping("/1/broadcast/dates")
    public BroadcastDateListResponse getDateList(@RequestParam(required = false, defaultValue = "14") int size) {
        return service.getBroadcastDateList(Pageable.ofSize(size));
    }

    @PatchMapping("/1/broadcast/{broadcastId}/status")
    public BroadcastResponse changeStatus(
            @PathVariable long broadcastId,
            @RequestBody BroadcastStatusRequest request,
            @CurrentMember MyBeautipUserDetails userDetails) {
        return service.changeStatus(broadcastId, request.getStatus(), userDetails.getMember().getId());
    }

    @PatchMapping("/1/broadcast/{broadcastId}/notification")
    public NotificationResponse setNotify(@PathVariable Long broadcastId,
                                          @RequestBody BooleanDto isNotifyNeeded,
                                          @CurrentMember MyBeautipUserDetails userDetails) {
        return service.setNotify(broadcastId, isNotifyNeeded.isBool(), userDetails.getMember().getId());
    }

    @PostMapping("/1/broadcast/{broadcastId}/report")
    public ReportCountResponse broadcastReport(@PathVariable long broadcastId,
                                               @CurrentMember MyBeautipUserDetails userDetails,
                                               @RequestBody BroadcastReportRequest request) {
        int reportCount = service.report(broadcastId, userDetails.getMember().getId(), request);

        return new ReportCountResponse(broadcastId, reportCount);
    }
}
