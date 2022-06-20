package com.jocoos.mybeautip.domain.event.api.front;

import com.jocoos.mybeautip.domain.event.dto.EventResponse;
import com.jocoos.mybeautip.global.wrapper.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/")
public class EventController {

    @GetMapping("/1/event/{eventId}")
    public ResultResponse<EventResponse> get(@PathVariable long eventId) {
        // TODO 이벤트 정보 조회 API 개발
        return new ResultResponse<>();
    }


}
