package com.jocoos.mybeautip.domain.event.api.front;

import com.jocoos.mybeautip.domain.event.dto.EventJoinHistoryResponse;
import com.jocoos.mybeautip.domain.event.dto.EventJoinResponse;
import com.jocoos.mybeautip.restapi.CursorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class EventJoinController {

    @PostMapping("/1/event/join/{eventId}")
    public EventJoinResponse joinEvent(@PathVariable long eventId) {
        return EventJoinResponse.builder()
                .result(2)
                .build();
    }

    @GetMapping("/1/event/join/history")
    public CursorResponse getJoinEventList(@RequestParam(required = false) Long cursor,
                                           @RequestParam(defaultValue = "20") int size) {

        List<EventJoinHistoryResponse> content = new ArrayList<>();
        EventJoinHistoryResponse joinEventResponse = EventJoinHistoryResponse.builder()
                .title("MockUp Title")
                .description("MockUp Description")
                .createdAt(LocalDateTime.now())
                .build();

        content.add(joinEventResponse);

        String nextCursor = "10";

        return new CursorResponse.Builder<>("/api/1/event/join", content)
                .withCount(size)
                .withCursor(nextCursor)
                .toBuild();
    }
}
