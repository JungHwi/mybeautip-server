package com.jocoos.mybeautip.domain.video.api.front;

import com.jocoos.mybeautip.domain.video.dto.VideoResponse;
import com.jocoos.mybeautip.domain.video.service.VideoService;
import com.jocoos.mybeautip.global.wrapper.CursorResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService service;

    @GetMapping("/1/videos")
    public ResponseEntity<CursorResultResponse<VideoResponse>> getVideos(@RequestParam(name = "category_id", defaultValue = "1") Integer categoryId,
                                                                         @RequestParam(defaultValue = "50") int count,
                                                                         @RequestParam(required = false) ZonedDateTime cursor) {

        Pageable pageable = PageRequest.of(0, count);

        List<VideoResponse> response = service.findVideos(categoryId, cursor, pageable);

        CursorResultResponse<VideoResponse> result = new CursorResultResponse<>(response);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/1/videos/{video_id}")
    public ResponseEntity<VideoResponse> getVideo(@PathVariable(name = "video_id") long videoId) {

        VideoResponse response = service.getVideo(videoId);

        return ResponseEntity.ok(response);
    }
}
