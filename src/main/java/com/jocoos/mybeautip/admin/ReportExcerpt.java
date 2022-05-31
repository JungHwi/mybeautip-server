package com.jocoos.mybeautip.admin;

import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.report.Report;
import org.springframework.data.rest.core.config.Projection;

import java.util.Date;

@Projection(name = "reported_member", types = Report.class)
public interface ReportExcerpt {

    String getReason();

    Member getYou();

    Member getMe();

    Date getCreatedAt();
}
