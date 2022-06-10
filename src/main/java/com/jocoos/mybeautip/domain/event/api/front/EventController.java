package com.jocoos.mybeautip.domain.event.api.front;

import com.jocoos.mybeautip.domain.event.dto.JoinEventResponse;
import com.jocoos.mybeautip.restapi.CursorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/")
public class EventController {

    @GetMapping("/1/event/join")
    public CursorResponse getJoinEventList(@RequestParam(required = false) Long cursor,
                                                              @RequestParam(defaultValue = "20") int size) {

        List<JoinEventResponse> content = new ArrayList<>();
        JoinEventResponse joinEventResponse = JoinEventResponse.builder()
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
