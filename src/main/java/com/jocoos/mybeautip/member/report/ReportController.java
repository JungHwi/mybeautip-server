package com.jocoos.mybeautip.member.report;

import com.jocoos.mybeautip.member.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(value = "/api/1/members/{id}/reports", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReportController {
  private static MemberService memberService;
  private static ReportRepository reportRepository;
  
  public ReportController(MemberService memberService, ReportRepository reportRepository) {
    this.memberService = memberService;
    this.reportRepository = reportRepository;
  }
  
  @PutMapping({"id"})
  public void reportMember(@PathVariable("id") Long id,
                           @Valid @RequestBody ReportRequest reportRequest) {
    Report report = new Report(memberService.currentMemberId(), id, reportRequest.getReason());
    reportRepository.save(report);
  }
}
