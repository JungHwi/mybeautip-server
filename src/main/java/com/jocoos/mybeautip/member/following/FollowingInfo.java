package com.jocoos.mybeautip.member.following;

import lombok.Data;
import lombok.NoArgsConstructor;

import com.jocoos.mybeautip.member.MemberInfo;

@Data
@NoArgsConstructor
public class FollowingInfo {
  public Long followingId;
  public Long createdAt;
  public MemberInfo member;
  
  public FollowingInfo(Following following) {
    this.followingId = following.getId();
    this.createdAt = following.getCreatedAt().getTime();
  }
}