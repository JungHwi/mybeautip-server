package com.jocoos.mybeautip.domain.event.api.front;

import com.jocoos.mybeautip.domain.event.code.EventStatus;
import com.jocoos.mybeautip.domain.event.code.EventType;
import com.jocoos.mybeautip.domain.event.dto.EventListResponse;
import com.jocoos.mybeautip.domain.event.dto.EventResponse;
import com.jocoos.mybeautip.domain.event.service.EventService;
import com.jocoos.mybeautip.video.LegacyVideoService;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.VideoRepository;
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
    public ResponseEntity<List<EventListResponse>> getEventList(
            @RequestParam(name = "event_type", required = false) EventType eventType,
            @RequestParam(name = "status", required = false) EventStatus status) {
        List<EventListResponse> response = eventService.getEventList(eventType, status);

        return ResponseEntity.ok(response);
    }

    // FIXME 임시로 100 파라미터로 접근 시 비디오 내려줌
    private final VideoRepository videoRepository;
    private final LegacyVideoService videoService;
    @GetMapping("/1/event/{eventId}")
    public ResponseEntity<?> get(@PathVariable long eventId) {

        if (eventId == 100) {
            Video video = videoRepository.findById(12255L).orElse(null);
            return ResponseEntity.ok(videoService.generateVideoInfo(video));
        }

        EventResponse result = eventService.getEvent(eventId);
        return ResponseEntity.ok(result);
    }
}
