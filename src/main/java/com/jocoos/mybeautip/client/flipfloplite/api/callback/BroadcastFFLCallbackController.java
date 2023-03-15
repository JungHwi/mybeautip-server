package com.jocoos.mybeautip.client.flipfloplite.api.callback;

import com.jocoos.mybeautip.client.flipfloplite.dto.FFLBroadcastChangeStatusRequest;
import com.jocoos.mybeautip.client.flipfloplite.service.FFLCallbackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/callback/ffl")
@RestController
public class BroadcastFFLCallbackController {

    private final FFLCallbackService service;

    // check flipflop lite api VideoRoom-상태-변경-App-Callback-API-알림
    @PostMapping("/change-status")
    public void callbackChangeStatus(@RequestBody FFLBroadcastChangeStatusRequest request) {
        service.changeStatus(request.data());
    }
}
