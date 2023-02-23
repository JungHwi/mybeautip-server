package com.jocoos.mybeautip.domain.broadcast.api.admin;

import com.jocoos.mybeautip.client.flipfloplite.FlipFlopLiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminBroadcastController {

    private final FlipFlopLiteService flipFlopLiteService;

    @PostMapping("/member/migration")
    public ResponseEntity<Integer> migration() {

        int response = flipFlopLiteService.migration();
        return ResponseEntity.ok(response);
    }
}
