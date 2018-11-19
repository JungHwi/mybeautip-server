package com.jocoos.mybeautip.admin;

import java.util.Date;

import org.springframework.data.rest.core.config.Projection;

import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.report.Report;

@Projection(name = "reported_member", types = Report.class)
public interface ReportExcerpt {

  String getReason();

  Member getYou();

  Member getMe();

  Date getCreatedAt();
}
