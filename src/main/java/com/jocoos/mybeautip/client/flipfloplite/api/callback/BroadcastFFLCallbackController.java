package com.jocoos.mybeautip.client.flipfloplite.api.callback;

import com.jocoos.mybeautip.client.flipfloplite.dto.FFLBroadcastChangeStatusRequest;
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

    @PostMapping("/change-status")
    public void callbackChangeStatus(@RequestBody FFLBroadcastChangeStatusRequest request) {
        log.debug("ffl change status callback {}", request);
    }
}
