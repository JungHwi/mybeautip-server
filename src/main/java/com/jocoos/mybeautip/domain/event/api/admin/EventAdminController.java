package com.jocoos.mybeautip.domain.event.api.admin;

import com.jocoos.mybeautip.domain.event.code.EventStatus;
import com.jocoos.mybeautip.domain.event.dto.AdminEventListResponse;
import com.jocoos.mybeautip.domain.event.dto.EventStatusResponse;
import com.jocoos.mybeautip.domain.event.dto.PageResponse;
import com.jocoos.mybeautip.domain.event.service.EventService;
import com.jocoos.mybeautip.domain.event.vo.EventSearchCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/")
@RestController
public class EventAdminController {

    private final EventService eventService;

    @GetMapping("/admin/event/status")
    public ResponseEntity<List<EventStatusResponse>> eventStates() {
        return ResponseEntity.ok(eventService.getEventStatesWithNum());
    }

    @GetMapping("/admin/event")
    public ResponseEntity<PageResponse<AdminEventListResponse>> events(
            @RequestParam(required = false) EventStatus status,
            @RequestParam(required = false, defaultValue = "1") Long page,
            @RequestParam(required = false, defaultValue = "10") Long size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) ZonedDateTime startAt,
            @RequestParam(required = false) ZonedDateTime endAt) {

        EventSearchCondition condition = EventSearchCondition.builder()
                .statuses(status == null ? null : Collections.singleton(status))
                .page(page - 1)
                .limit(size)
                .sort(sort)
                .search(search)
                .keyword(keyword)
                .startAt(startAt)
                .endAt(endAt)
                .build();

        return ResponseEntity.ok(eventService.getEvents(condition));
    }
}
