package com.jocoos.mybeautip.domain.event.api.front;

import com.jocoos.mybeautip.domain.event.code.EventType;
import com.jocoos.mybeautip.domain.event.dto.EventListResponse;
import com.jocoos.mybeautip.domain.event.dto.EventResponse;
import com.jocoos.mybeautip.domain.event.service.EventService;
import com.jocoos.mybeautip.global.wrapper.CursorResultResponse;
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
    public ResponseEntity<CursorResultResponse<EventListResponse>> getEventList(@RequestParam(name = "event_type", required = false) EventType eventType,
                                                                               @RequestParam(required = false, defaultValue = MAX_LONG_STRING) Long cursor,
                                                                               @RequestParam(defaultValue = "20") int size) {
        List<EventListResponse> response = eventService.getEventList(eventType, cursor, size);

        CursorResultResponse<EventListResponse> result = new CursorResultResponse<>(response);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/1/event/{eventId}")
    public ResponseEntity get(@PathVariable long eventId) {
        EventResponse result = eventService.getEvent(eventId);
        return ResponseEntity.ok(result);
    }
}
