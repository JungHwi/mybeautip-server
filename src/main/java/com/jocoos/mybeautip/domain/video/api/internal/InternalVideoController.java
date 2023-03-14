package com.jocoos.mybeautip.domain.video.api.internal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;

import com.jocoos.mybeautip.domain.video.dto.VideoResponse;
import com.jocoos.mybeautip.domain.video.dto.VideoViewResponse;
import com.jocoos.mybeautip.domain.video.service.VideoService;
import com.jocoos.mybeautip.global.annotation.CurrentMember;
import com.jocoos.mybeautip.global.wrapper.CursorResultResponse;
import com.jocoos.mybeautip.security.MyBeautipUserDetails;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
public class InternalVideoController {

    private final VideoService service;

    @GetMapping("/1/videos")
    public ResponseEntity<CursorResultResponse<VideoResponse>> getVideos(@RequestParam(name = "category_id", defaultValue = "1") Integer categoryId,
                                                                         @RequestParam(defaultValue = "20") int count,
                                                                         @RequestParam(required = false) ZonedDateTime cursor) {

        List<VideoResponse> response = service.findVideos(categoryId, cursor, count);

        CursorResultResponse<VideoResponse> result = new CursorResultResponse<>(response);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/1/videos/{video_id}")
    public ResponseEntity<VideoResponse> getVideo(@PathVariable(name = "video_id") long videoId) {

        VideoResponse response = service.getVideo(videoId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/1/videos/recommend")
    public ResponseEntity<List<VideoResponse>> getRecommendedVideos() {
        return ResponseEntity.ok(service.findRecommendedVideos());
    }

    @PatchMapping("/1/video/{videoId}/view-count")
    public ResponseEntity<VideoViewResponse> addVideoViewCount(@CurrentMember MyBeautipUserDetails userDetails,
                                                               @PathVariable Long videoId) {
        String username = userDetails == null ? null : userDetails.getUsername();
        return ResponseEntity.ok(service.addViewCount(videoId, username));
    }
}
