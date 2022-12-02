package com.jocoos.mybeautip.domain.home.api.front;

import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType;
import com.jocoos.mybeautip.domain.community.dto.CommunityResponse;
import com.jocoos.mybeautip.domain.home.dto.TopSummaryResponse;
import com.jocoos.mybeautip.domain.home.service.SummaryService;
import com.jocoos.mybeautip.domain.video.dto.VideoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/1/summary/community/top")
    public ResponseEntity<TopSummaryResponse> communityTopSummary() {
        return ResponseEntity.ok(summaryService.communityTop());
    }

    @GetMapping("/1/summary/community/{type}")
    public ResponseEntity<List<CommunityResponse>> communitySummary(@PathVariable CommunityCategoryType type) {
        return ResponseEntity.ok(summaryService.community(type));
    }

    @GetMapping("/1/summary/video")
    public ResponseEntity<List<VideoResponse>> videoSummary() {
        return ResponseEntity.ok(summaryService.video());
    }
}
