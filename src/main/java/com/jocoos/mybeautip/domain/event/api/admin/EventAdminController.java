package com.jocoos.mybeautip.domain.event.api.admin;

import com.jocoos.mybeautip.domain.event.dto.EventStatusResponse;
import com.jocoos.mybeautip.domain.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
