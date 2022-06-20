package com.jocoos.mybeautip.domain.event.api.front;


import com.jocoos.mybeautip.domain.event.dto.EventHistory;
import com.jocoos.mybeautip.restapi.CursorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/")
public class EventHistoryController {

    @GetMapping("/1/event/history")
    public CursorResponse<EventHistory> getEvent(@RequestParam(required = false, defaultValue = "20") int size,
                                                 @RequestParam(required = false) Long eventHistoryId) {
        // TODO 내 이벤트 이력 API 개발
        List<EventHistory> eventHistoryList = new ArrayList<>();
        EventHistory eventHistory = EventHistory.builder()
                .id(1L)
                .title("Mock up title")
                .description("Mock up description")
                .createdAt(ZonedDateTime.now())
                .build();

        eventHistoryList.add(eventHistory);

        return new CursorResponse.Builder<>("/api/1/event", eventHistoryList)
                .toBuild();
    }
}
