package com.jocoos.mybeautip.domain.home.api.front;

import com.jocoos.mybeautip.domain.home.dto.CommunitySummaryResponse;
import com.jocoos.mybeautip.domain.home.service.SummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class SummaryController {

    private final SummaryService summaryService;


    @GetMapping("/1/summary/community")
    public ResponseEntity<CommunitySummaryResponse> communitySummary() {
        return ResponseEntity.ok(summaryService.summary());
    }
}
