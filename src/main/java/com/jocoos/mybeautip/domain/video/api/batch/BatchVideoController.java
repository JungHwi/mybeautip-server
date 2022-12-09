package com.jocoos.mybeautip.domain.video.api.batch;

import com.jocoos.mybeautip.domain.video.service.BatchVideoService;
import com.jocoos.mybeautip.global.dto.single.LongDto;
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
    public ResponseEntity<LongDto> openReservationVideos() {
        return ResponseEntity.ok(new LongDto(service.openVideos()));
    }

}
