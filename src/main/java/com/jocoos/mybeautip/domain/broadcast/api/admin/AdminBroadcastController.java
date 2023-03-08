package com.jocoos.mybeautip.domain.broadcast.api.admin;

import com.jocoos.mybeautip.client.flipfloplite.FlipFlopLiteService;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus;
import com.jocoos.mybeautip.domain.broadcast.dto.AdminBroadcastResponse;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastPatchRequest;
import com.jocoos.mybeautip.domain.broadcast.service.AdminBroadcastService;
import com.jocoos.mybeautip.domain.search.dto.CountResponse;
import com.jocoos.mybeautip.global.dto.single.IdDto;
import com.jocoos.mybeautip.global.vo.SearchOption;
import com.jocoos.mybeautip.global.wrapper.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

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

    @GetMapping("/broadcast")
    public PageResponse<AdminBroadcastResponse> getList(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "5") int size,
            @RequestParam(required = false) BroadcastStatus status,
            @RequestParam(required = false, name = "start_at") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startAt,
            @RequestParam(required = false, name = "end_at") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endAt,
            @RequestParam(required = false, defaultValue = "sortedStatus") String sort,
            @RequestParam(required = false, defaultValue = "DESC") Direction order,
            @RequestParam(required = false, name = "search_field") String searchField,
            @RequestParam(required = false, name = "search_keyword") String searchKeyword,
            @RequestParam(required = false, name = "is_reported") Boolean isReported) {
        SearchOption searchOption = SearchOption.builder()
                .startAt(startAt)
                .endAt(endAt)
                .zoneId(ZoneId.of("Asia/Seoul"))
                .searchField(searchField)
                .keyword(searchKeyword)
                .isReported(isReported)
                .build();

        return service.getList(status, searchOption, PageRequest.of(page - 1, size));
    }

    @GetMapping("/broadcast/{broadcastId}")
    public AdminBroadcastResponse get(@PathVariable long broadcastId) {
        return service.get(broadcastId);
    }

    // TODO start_at default 값 논의 필요
    @GetMapping("/broadcast/report-count")
    public CountResponse countReportedBroadcast(@RequestParam(required = false, name = "start_at") ZonedDateTime startAt) {
        return service.countReportedBroadcast(startAt);
    }

    @PatchMapping("/broadcast/{broadcastId}")
    public IdDto edit(@PathVariable long broadcastId, @RequestBody BroadcastPatchRequest request) {
        return new IdDto(service.edit(broadcastId, request));
    }

    // TODO shutdown or change status api need to choose
    @PatchMapping("/broadcast/{broadcastId}/shutdown")
    public void shutdown(@PathVariable long broadcastId) {
        service.shutdown(broadcastId);
    }
}
