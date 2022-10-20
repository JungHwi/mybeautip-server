package com.jocoos.mybeautip.domain.event.api.admin;

import com.jocoos.mybeautip.domain.event.code.EventStatus;
import com.jocoos.mybeautip.domain.event.dto.AdminEventListResponse;
import com.jocoos.mybeautip.domain.event.dto.EventResponse;
import com.jocoos.mybeautip.domain.event.dto.EventStatusResponse;
import com.jocoos.mybeautip.domain.event.dto.PageResponse;
import com.jocoos.mybeautip.domain.event.service.EventService;
import com.jocoos.mybeautip.domain.event.vo.EventSearchCondition;
import com.jocoos.mybeautip.domain.event.vo.Paging;
import com.jocoos.mybeautip.domain.event.vo.SearchKeyword;
import com.jocoos.mybeautip.domain.event.vo.Sort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/")
@RestController
public class AdminEventController {

    private final EventService eventService;

    @GetMapping("/admin/event/status")
    public ResponseEntity<List<EventStatusResponse>> getEventStates() {
        return ResponseEntity.ok(eventService.getEventStatesWithNum());
    }

    @GetMapping("/admin/event")
    public ResponseEntity<PageResponse<AdminEventListResponse>> getEvents(
            @RequestParam(required = false) EventStatus status,
            @RequestParam(required = false, defaultValue = "1") Long page,
            @RequestParam(required = false, defaultValue = "10") Long size,
            @RequestParam(required = false, defaultValue = "createdAt") String sort,
            @RequestParam(required = false, defaultValue = "DESC") String order,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) ZonedDateTime startAt,
            @RequestParam(required = false) ZonedDateTime endAt) {

        EventSearchCondition condition = EventSearchCondition.builder()
                .statuses(status == null ? null : Collections.singleton(status))
                .searchKeyword(SearchKeyword.from(search))
                .startAt(startAt)
                .endAt(endAt)
                .paging(new Paging(page - 1, size))
                .sort(new Sort(sort, order))
                .build();

        return ResponseEntity.ok(eventService.getEvents(condition));
    }

    @GetMapping("/admin/event/{eventId}")
    public ResponseEntity<EventResponse> getEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventService.getEvent(eventId));
    }
}
