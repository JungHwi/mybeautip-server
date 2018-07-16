package com.jocoos.mybeautip.member.following;

import com.jocoos.mybeautip.member.MemberController;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FollowingInfo {
  public Long followingId;
  public Long createdAt;
  public MemberController.MemberInfo member;
  
  public FollowingInfo(Following following) {
    this.followingId = following.getId();
    this.createdAt = following.getCreatedAt().getTime();
  }
}