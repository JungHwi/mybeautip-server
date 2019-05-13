package com.jocoos.mybeautip.admin;

import java.util.Date;

import org.springframework.data.rest.core.config.Projection;

import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.point.MemberPoint;

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
