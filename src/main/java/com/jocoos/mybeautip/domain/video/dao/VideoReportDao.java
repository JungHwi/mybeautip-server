package com.jocoos.mybeautip.domain.video.dao;

import com.jocoos.mybeautip.domain.slack.aspect.annotation.SendSlack;
import com.jocoos.mybeautip.video.report.VideoReport;
import com.jocoos.mybeautip.video.report.VideoReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.jocoos.mybeautip.domain.slack.aspect.code.MessageType.VIDEO_REPORT;

@RequiredArgsConstructor
@Service
public class VideoReportDao {

    private final VideoReportRepository videoReportRepository;

    @SendSlack(messageType = VIDEO_REPORT)
    @Transactional
    public VideoReport save(VideoReport videoReport) {
       return videoReportRepository.save(videoReport);
    }
}
