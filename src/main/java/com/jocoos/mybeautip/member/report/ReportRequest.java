package com.jocoos.mybeautip.member.report;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ReportRequest {
  @NotNull
  private Long memberId;
  
  @NotNull
  @Size(min = 1, max = 400)
  private String reason = "";
}