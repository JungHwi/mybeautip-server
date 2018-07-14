package com.jocoos.mybeautip.follow.member;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Size;

@Data
public class FollowListRequest {
  @Size(max = 13, message = "Invalid cursor")
  private String cursor = "";
  
  @Max(200)
  private Integer count = 50;
}