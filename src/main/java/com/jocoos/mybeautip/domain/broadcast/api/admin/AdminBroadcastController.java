package com.jocoos.mybeautip.domain.broadcast.api.admin;

import com.jocoos.mybeautip.client.flipfloplite.FlipFlopLiteService;
import com.jocoos.mybeautip.client.flipfloplite.code.FFLChatRoomDirectMessageCustomType;
import com.jocoos.mybeautip.client.flipfloplite.code.FFLChatRoomDirectMessageType;
import com.jocoos.mybeautip.client.flipfloplite.dto.FFLDirectMessageRequest;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastSortField;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus;
import com.jocoos.mybeautip.domain.broadcast.dto.AdminBroadcastResponse;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastPatchRequest;
import com.jocoos.mybeautip.domain.broadcast.service.AdminBroadcastService;
import com.jocoos.mybeautip.domain.search.dto.CountResponse;
import com.jocoos.mybeautip.global.annotation.CurrentMember;
import com.jocoos.mybeautip.global.dto.single.IdDto;
import com.jocoos.mybeautip.global.vo.SearchOption;
import com.jocoos.mybeautip.global.wrapper.PageResponse;
import com.jocoos.mybeautip.security.MyBeautipUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminBroadcastController {

    private final FlipFlopLiteService flipFlopLiteService;
    private final AdminBroadcastService service;

    @PostMapping("/member/migration")
    public ResponseEntity<Integer> migration() {
        int response = flipFlopLiteService.migration();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/test")
    public ResponseEntity test() {
        FFLDirectMessageRequest directRequest = new FFLDirectMessageRequest(FFLChatRoomDirectMessageType.COMMAND, FFLChatRoomDirectMessageCustomType.NO_CHAT,  List.of("1", "2"), "a", null);
        flipFlopLiteService.directMessage(8, directRequest);

        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/broadcast")
    public PageResponse<AdminBroadcastResponse> getList(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "5") int size,
            @RequestParam(required = false) BroadcastStatus status,
            @RequestParam(required = false, name = "start_at") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startAt,
            @RequestParam(required = false, name = "end_at") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endAt,
            @RequestParam(required = false, defaultValue = "SORTED_STATUS") BroadcastSortField sort,
            @RequestParam(required = false, defaultValue = "ASC") Direction order,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, name = "is_reported") Boolean isReported) {

        SearchOption searchOption = SearchOption.builder()
                .startAt(startAt)
                .endAt(endAt)
                .zoneId(ZoneId.of("Asia/Seoul"))
                .searchQueryString(search)
                .isReported(isReported)
                .build();

        Pageable pageable = PageRequest.of(page - 1, size, sort.getSort(order));

        return service.getList(status, searchOption, pageable);
    }

    @GetMapping("/broadcast/{broadcastId}")
    public AdminBroadcastResponse get(@PathVariable Long broadcastId,
                                      @CurrentMember MyBeautipUserDetails userDetails) {
        return service.get(broadcastId, userDetails.getUsername());
    }

    // TODO start_at default 값 논의 필요
    @GetMapping("/broadcast/report-count")
    public CountResponse countReportedBroadcast(@RequestParam(required = false, name = "start_at") ZonedDateTime startAt) {
        return service.countReportedBroadcast(startAt);
    }

    @PatchMapping("/broadcast/{broadcastId}")
    public IdDto edit(@PathVariable long broadcastId,
                      @RequestBody BroadcastPatchRequest request,
                      @CurrentMember MyBeautipUserDetails userDetails) {
        return new IdDto(service.edit(broadcastId, request, userDetails.getMember().getId()));
    }

    @PatchMapping("/broadcast/{broadcastId}/shutdown")
    public void shutdown(@PathVariable long broadcastId) {
        service.shutdown(broadcastId);
    }
}
