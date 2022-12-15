package com.jocoos.mybeautip.domain.notification.api.batch;

import com.jocoos.mybeautip.domain.notification.service.impl.NoLogin2WeeksNotificationService;
import com.jocoos.mybeautip.global.dto.single.IntegerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/batch/1/notification")
public class BatchNotificationController {

    private final NoLogin2WeeksNotificationService noLogin2WeeksNotificationService;

    @PostMapping
    public ResponseEntity<IntegerDto> noLogin2Weeks() {
        int count = noLogin2WeeksNotificationService.occurs();
        return ResponseEntity.ok().body(new IntegerDto(count));
    }
}
