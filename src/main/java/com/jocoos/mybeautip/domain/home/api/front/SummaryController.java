package com.jocoos.mybeautip.domain.home.api.front;

import com.jocoos.mybeautip.domain.home.dto.CommunitySummaryResponse;
import com.jocoos.mybeautip.domain.home.service.SummaryService;
import com.jocoos.mybeautip.domain.video.dto.VideoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/*
메인 페이지 데이터 내려주는 로직
 */
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class SummaryController {

    private final SummaryService summaryService;

    @GetMapping("/1/summary/community")
    public ResponseEntity<CommunitySummaryResponse> communitySummary() {
        return ResponseEntity.ok(summaryService.summaryCommunity());
    }

    @GetMapping("/1/summary/video")
    public ResponseEntity<List<VideoResponse>> videoSummary() {
        return ResponseEntity.ok(summaryService.summaryVideo());
    }
}
