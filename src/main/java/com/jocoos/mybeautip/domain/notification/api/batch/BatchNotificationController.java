package com.jocoos.mybeautip.domain.notification.api.batch;

import com.jocoos.mybeautip.domain.notification.service.impl.NoLogin2WeeksNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/batch/1/notification")
public class BatchNotificationController {

    private final NoLogin2WeeksNotificationService noLogin2WeeksNotificationService;

    @Async
    @PostMapping
    public ResponseEntity noLogin2Weeks() {
        noLogin2WeeksNotificationService.occurs();
        return new ResponseEntity(HttpStatus.OK);
    }
}
