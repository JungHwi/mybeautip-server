package com.jocoos.mybeautip.member.report;

import lombok.Data;

import javax.validation.constraints.Max;

@Data
public class ReportRequest {
  @Max(4)
  private String reason = "";
}