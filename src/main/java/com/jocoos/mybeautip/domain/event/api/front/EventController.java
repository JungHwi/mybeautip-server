package com.jocoos.mybeautip.domain.event.api.front;

import com.jocoos.mybeautip.domain.event.dto.EventListResponse;
import com.jocoos.mybeautip.domain.event.dto.EventResponse;
import com.jocoos.mybeautip.domain.event.service.EventService;
import com.jocoos.mybeautip.restapi.CursorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.jocoos.mybeautip.global.constant.MybeautipConstant.MAX_LONG_STRING;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/")
public class EventController {

    private final EventService eventService;

    @GetMapping("/1/event")
    public ResponseEntity getEventList(@RequestParam(required = false, defaultValue = MAX_LONG_STRING) Long cursor,
                                       @RequestParam(defaultValue = "20") int size) {
        List<EventListResponse> result = eventService.getEventList(cursor, size);
        CursorResponse cursorResponse = new CursorResponse.Builder<>("/api/3/event/", result)
                .withCount(size)
                .withCursor(result.size() > 0 ? String.valueOf(result.get(result.size() - 1).getId()) : null)
                .toBuild();

        return ResponseEntity.ok(cursorResponse);
    }

    @GetMapping("/1/event/{eventId}")
    public ResponseEntity get(@PathVariable long eventId) {
        EventResponse result = eventService.getEvent(eventId);
        return ResponseEntity.ok(result);
    }
}
