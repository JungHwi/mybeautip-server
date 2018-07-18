package com.jocoos.mybeautip.member.following;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class FollowingMemberRequest {
  @NotNull
  private Long memberId;
}