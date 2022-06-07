package com.jocoos.mybeautip.admin;

import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.report.VideoReport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import java.util.Date;

@Projection(name = "video_report_details", types = VideoReport.class)
public interface VideoReportExcerpt {

    Long getId();

    Video getVideo();

    @Value("#{target.video.member}")
    Member getVideoOwner();

    @Value("#{target.createdBy}")
    Member getMember();

    String getReason();

    Date getCreatedAt();
}
