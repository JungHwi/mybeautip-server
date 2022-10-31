package com.jocoos.mybeautip.domain.event.api.admin;

import com.jocoos.mybeautip.domain.event.code.EventStatus;
import com.jocoos.mybeautip.domain.event.dto.AdminEventResponse;
import com.jocoos.mybeautip.domain.event.dto.EventStatusResponse;
import com.jocoos.mybeautip.global.wrapper.PageResponse;
import com.jocoos.mybeautip.domain.event.service.AdminEventService;
import com.jocoos.mybeautip.domain.event.vo.EventSearchCondition;
import com.jocoos.mybeautip.global.vo.Paging;
import com.jocoos.mybeautip.global.vo.SearchKeyword;
import com.jocoos.mybeautip.global.vo.Sort;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/admin/")
@RestController
public class AdminEventController {

    private final AdminEventService service;

    @GetMapping("/event/status")
    public ResponseEntity<List<EventStatusResponse>> getEventStates() {
        return ResponseEntity.ok(service.getEventStates());
    }

    @GetMapping("/event")
    public ResponseEntity<PageResponse<AdminEventResponse>> getEvents(
            @RequestParam(required = false) EventStatus status,
            @RequestParam(required = false, defaultValue = "1") Long page,
            @RequestParam(required = false, defaultValue = "10") Long size,
            @RequestParam(required = false, defaultValue = "createdAt") String sort,
            @RequestParam(required = false, defaultValue = "DESC") String order,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startAt,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endAt) {

        EventSearchCondition condition = EventSearchCondition.builder()
                .statuses(status == null ? null : Collections.singleton(status))
                .searchKeyword(SearchKeyword.from(search, startAt, endAt, ZoneId.of("Asia/Seoul")))
                .paging(Paging.page(page - 1, size))
                .sort(new Sort(sort, order))
                .build();

        return ResponseEntity.ok(service.getEvents(condition));
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<AdminEventResponse> getEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(service.getEvent(eventId));
    }
}
