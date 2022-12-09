package com.jocoos.mybeautip.domain.video.api.batch;

import com.jocoos.mybeautip.domain.video.dto.VideoOpenBatchResult;
import com.jocoos.mybeautip.domain.video.service.BatchVideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/batch")
@RestController
public class BatchVideoController {

    private final BatchVideoService service;

    @PatchMapping("/video/open")
    public ResponseEntity<VideoOpenBatchResult> openReservationVideos() {
        return ResponseEntity.ok(service.openVideos());
    }

}
