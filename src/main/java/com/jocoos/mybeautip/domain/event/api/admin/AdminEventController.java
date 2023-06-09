package com.jocoos.mybeautip.domain.event.api.admin;

import com.jocoos.mybeautip.domain.event.code.EventStatus;
import com.jocoos.mybeautip.domain.event.dto.AdminEventResponse;
import com.jocoos.mybeautip.domain.event.dto.EditEventRequest;
import com.jocoos.mybeautip.domain.event.dto.EventRequest;
import com.jocoos.mybeautip.domain.event.dto.EventStatusResponse;
import com.jocoos.mybeautip.domain.event.service.AdminEventService;
import com.jocoos.mybeautip.domain.event.vo.EventSearchCondition;
import com.jocoos.mybeautip.global.dto.single.BooleanDto;
import com.jocoos.mybeautip.global.dto.single.IdDto;
import com.jocoos.mybeautip.global.vo.Paging;
import com.jocoos.mybeautip.global.vo.SearchOption;
import com.jocoos.mybeautip.global.vo.Sort;
import com.jocoos.mybeautip.global.wrapper.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/admin")
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
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "createdAt") String sort,
            @RequestParam(required = false, defaultValue = "DESC") String order,
            @RequestParam(required = false) String search,
            @RequestParam(name = "start_at", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startAt,
            @RequestParam(name = "end_at", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endAt,
            @RequestParam(name = "community_category_id", required = false) Long communityCategoryId,
            @RequestParam(name = "is_top_fix", required = false) Boolean isTopFix) {

        SearchOption searchOption = SearchOption.builder()
                .searchQueryString(search)
                .startAt(startAt)
                .endAt(endAt)
                .zoneId(ZoneId.of("Asia/Seoul"))
                .isTopFix(isTopFix)
                .build();

        EventSearchCondition condition = EventSearchCondition.builder()
                .statuses(status == null ? null : Collections.singleton(status))
                .communityCategoryId(communityCategoryId)
                .searchOption(searchOption)
                .paging(Paging.offsetBased(page - 1, size))
                .sort(new Sort(sort, order))
                .build();

        return ResponseEntity.ok(service.getEvents(condition));
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<AdminEventResponse> getEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(service.getEvent(eventId));
    }

    @PostMapping("/event")
    public ResponseEntity<AdminEventResponse> createEvent(@RequestBody @Valid EventRequest request) {
        AdminEventResponse response = service.create(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/event/{event_id}")
    public ResponseEntity<AdminEventResponse> editEvent(@PathVariable(name = "event_id") long eventId,
                                                        @RequestBody @Valid EditEventRequest request) {
        request.setId(eventId);

        AdminEventResponse response = service.edit(request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/event/{eventId}/hide")
    public ResponseEntity<IdDto> hideEvent(@PathVariable Long eventId, @RequestBody BooleanDto isHide) {
        return ResponseEntity.ok(new IdDto(service.hide(eventId, isHide.isBool())));
    }

    @DeleteMapping("/event/{eventId}")
    public ResponseEntity<IdDto> deleteEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(new IdDto(service.delete(eventId)));
    }
}
