package com.jocoos.mybeautip.restapi;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Size;

@Data
public class CursorRequest {
  @Size(max = 13)
  private String cursor = "";

  @Max(100)
  private Integer count = 20;
}