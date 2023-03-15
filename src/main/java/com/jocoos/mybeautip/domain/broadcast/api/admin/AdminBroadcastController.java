package com.jocoos.mybeautip.domain.broadcast.api.admin;

import com.jocoos.mybeautip.client.flipfloplite.FlipFlopLiteService;
import com.jocoos.mybeautip.client.flipfloplite.code.FFLChatRoomBroadcastMessageCustomType;
import com.jocoos.mybeautip.client.flipfloplite.code.FFLChatRoomBroadcastMessageType;
import com.jocoos.mybeautip.client.flipfloplite.code.FFLChatRoomDirectMessageCustomType;
import com.jocoos.mybeautip.client.flipfloplite.code.FFLChatRoomDirectMessageType;
import com.jocoos.mybeautip.client.flipfloplite.dto.FFLBroadcastMessageRequest;
import com.jocoos.mybeautip.client.flipfloplite.dto.FFLDirectMessageRequest;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastSortField;
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
        FFLBroadcastMessageRequest request = new FFLBroadcastMessageRequest(FFLChatRoomBroadcastMessageType.COMMAND, FFLChatRoomBroadcastMessageCustomType.UPDATE, "a", List.of("1", "2"));
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

        Pageable pageable = PageRequest.of(page - 1, size, sort.getSort(order));

        return service.getList(status, searchOption, pageable);
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
