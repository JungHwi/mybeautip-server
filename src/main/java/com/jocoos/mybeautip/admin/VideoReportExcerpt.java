package com.jocoos.mybeautip.admin;

import java.util.Date;

import org.springframework.data.rest.core.config.Projection;

import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.report.VideoReport;

@Projection(name = "reported_motd", types = VideoReport.class)
public interface VideoReportExcerpt {

  Long getId();

  Video getVideo();

  Member getMember();

  String getReason();

  Date getCreatedAt();
}
