package com.jocoos.mybeautip.domain.broadcast.api.front;

import com.jocoos.mybeautip.client.flipfloplite.FlipFlopLiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class BroadcastController {

    private final FlipFlopLiteService flipFlopLiteService;

}
