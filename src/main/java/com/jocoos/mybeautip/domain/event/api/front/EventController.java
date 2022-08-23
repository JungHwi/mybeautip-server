package com.jocoos.mybeautip.domain.event.api.front;

import com.jocoos.mybeautip.domain.event.code.EventType;
import com.jocoos.mybeautip.domain.event.dto.EventListResponse;
import com.jocoos.mybeautip.domain.event.dto.EventResponse;
import com.jocoos.mybeautip.domain.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/")
public class EventController {

    private final EventService eventService;

    @GetMapping("/1/event")
    public ResponseEntity<List<EventListResponse>> getEventList(@RequestParam(name = "event_type", required = false) EventType eventType) {
        List<EventListResponse> response = eventService.getEventList(eventType);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/1/event/{eventId}")
    public ResponseEntity<EventResponse> get(@PathVariable long eventId) {
        EventResponse result = eventService.getEvent(eventId);
        return ResponseEntity.ok(result);
    }
}
