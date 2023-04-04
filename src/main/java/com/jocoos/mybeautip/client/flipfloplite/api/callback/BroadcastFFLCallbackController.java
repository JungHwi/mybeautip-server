package com.jocoos.mybeautip.client.flipfloplite.api.callback;

import com.jocoos.mybeautip.client.flipfloplite.dto.FFLCallbackRequest;
import com.jocoos.mybeautip.client.flipfloplite.service.FFLCallbackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/callback/ffl")
@RestController
public class BroadcastFFLCallbackController {

    private final FFLCallbackService service;

    // check flipflop lite api VideoRoom-상태-변경-App-Callback-API-알림, StreamKey 상태 변경 App Callback API 알림
    @PostMapping
    public void callback(
            @RequestHeader(value = "X-Request-Id", required = false) String requestId,
            @RequestBody FFLCallbackRequest request) {
        log.debug("FFL Callback Start Request Id : {}, Request Body : {}", requestId, request);
        service.callback(requestId, request.type(), request.data());
        log.debug("FFL Callback End Request Id : {}", requestId);
    }
}
