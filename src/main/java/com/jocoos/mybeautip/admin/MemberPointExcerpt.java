package com.jocoos.mybeautip.admin;

import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.point.MemberPoint;
import org.springframework.data.rest.core.config.Projection;

import java.util.Date;

@Projection(name = "point_detail", types = {MemberPoint.class})
public interface MemberPointExcerpt {

    Long getId();

    Member getMember();

    int getState();

    int getPoint();

    Date getCreatedAt();

    Date getExpiryAt();

    Date getExpiredAt();
}
