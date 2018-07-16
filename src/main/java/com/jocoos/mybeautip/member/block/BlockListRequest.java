package com.jocoos.mybeautip.member.block;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Size;

@Data
public class BlockListRequest {
  @Size(max = 13)
  private String cursor = "";
  
  @Max(200)
  private Integer count = 50;
}