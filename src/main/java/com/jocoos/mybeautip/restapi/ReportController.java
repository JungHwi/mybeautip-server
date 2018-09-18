package com.jocoos.mybeautip.restapi;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.MemberNotFoundException;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.member.report.Report;
import com.jocoos.mybeautip.member.report.ReportRepository;

@Slf4j
@RestController
@RequestMapping(value = "/api/1/members/me/reports", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReportController {
  private final MemberService memberService;
  private final ReportRepository reportRepository;
  
  public ReportController(MemberService memberService, ReportRepository reportRepository) {
    this.memberService = memberService;
    this.reportRepository = reportRepository;
  }
  
  @PutMapping
  public void reportMember(@Valid @RequestBody ReportRequest reportRequest,
                           BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      log.debug("bindingResult: {}", bindingResult);
      throw new BadRequestException("invalid report request");
    }

    long me = memberService.currentMemberId();
    long you = reportRequest.getMemberId();
    Report report = new Report(me, you, reportRequest.getReason());
    Optional<Report> optional = reportRepository.findByMeAndYou(me, you);
    if (optional.isPresent()) {
      throw new BadRequestException("Already reported.");
    }

    reportRepository.save(report);
  }

  @GetMapping("/{id:.+}")
  public ReportResponse didReport(@PathVariable Integer id) {
    Long me = memberService.currentMemberId();
    if (me == null) {
      throw new MemberNotFoundException("Login required");
    }

    ReportResponse response = new ReportResponse(false);
    reportRepository.findByMeAndYou(me, id)
      .ifPresent(report -> response.setReported(true));

    return response;
  }

  @Data
  static class ReportRequest {
    @NotNull
    private Long memberId;

    @NotNull
    @Size(min = 1, max = 400)
    private String reason = "";
  }

  @Data
  @AllArgsConstructor
  class ReportResponse {
    Boolean reported;
  }
}