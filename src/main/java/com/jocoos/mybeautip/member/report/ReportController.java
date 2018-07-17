package com.jocoos.mybeautip.member.report;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.member.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping(value = "/api/1/members/me/reports", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReportController {
  private static MemberService memberService;
  private static ReportRepository reportRepository;
  
  public ReportController(MemberService memberService, ReportRepository reportRepository) {
    this.memberService = memberService;
    this.reportRepository = reportRepository;
  }
  
  @PutMapping()
  public void reportMember(@Valid @RequestBody ReportRequest reportRequest) {
    long me = memberService.currentMemberId();
    long you = reportRequest.getMemberId();
    Report report = new Report(me, you, reportRequest.getReason());
    Optional<Report> optional = reportRepository.findByMeAndYou(me, you);
    if (optional.isPresent()) {
      throw new BadRequestException("Already reported.");
    }
    reportRepository.save(report);
  }
}
