package com.jocoos.mybeautip.domain.event.api.front;

import com.jocoos.mybeautip.domain.event.dto.ParticipationResponse;
import com.jocoos.mybeautip.global.wrapper.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/")
public class ParticipationController {

    @PostMapping("/1/event/{eventId}")
    public ResultResponse<ParticipationResponse> participation(@PathVariable long eventId) {
        // TODO 이벤트에 참여하는 API 개발
        ParticipationResponse result = new ParticipationResponse(1);
        return new ResultResponse<>(result);
    }
}
