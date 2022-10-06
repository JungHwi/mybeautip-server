package com.jocoos.mybeautip.domain.home.service;

import com.jocoos.mybeautip.domain.home.dto.CommunitySummaryResponse;
import com.jocoos.mybeautip.domain.home.service.community.CommunitySummary;
import com.jocoos.mybeautip.domain.home.service.video.VideoSummary;
import com.jocoos.mybeautip.domain.video.dto.VideoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jocoos.mybeautip.domain.home.code.SummaryCount.VIDEO_SUMMARY;

@RequiredArgsConstructor
@Service
public class SummaryService {

    private final CommunitySummary communitySummary;
    private final VideoSummary videoSummary;

    @Transactional(readOnly = true)
    public CommunitySummaryResponse summaryCommunity() {
        return communitySummary.summaryCommunity();
    }

    @Transactional(readOnly = true)
    public List<VideoResponse> summaryVideo() {
        return videoSummary.summaryVideo(VIDEO_SUMMARY.getCount());
    }
}
