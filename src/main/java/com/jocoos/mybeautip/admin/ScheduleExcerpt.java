package com.jocoos.mybeautip.admin;

import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.schedules.Schedule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import java.util.Date;

@Projection(name = "schedule_details", types = Schedule.class)
public interface ScheduleExcerpt {

    Long getId();

    String getTitle();

    String getThumbnailUrl();

    @Value("#{target.createdBy}")
    Member getMember();

    Date getCreatedAt();

    Date getStartedAt();

    String getInstantTitle();

    String getInstantMessage();
}
