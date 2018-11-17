package com.jocoos.mybeautip.restapi;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.MemberNotFoundException;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.member.report.Report;
import com.jocoos.mybeautip.member.report.ReportRepository;
import com.jocoos.mybeautip.notification.MessageService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/api/1/members/me/reports", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReportController {
  private final MemberService memberService;
  private final MessageService messageService;
  private final MemberRepository memberRepository;
  private final ReportRepository reportRepository;

  private static final String MEMBER_NOT_FOUND = "member.not_found";
  private static final String MEMBER_ALREADY_REPORTED = "member.already_reported";

  public ReportController(MemberService memberService,
                          MessageService messageService,
                          MemberRepository memberRepository,
                          ReportRepository reportRepository) {
    this.memberService = memberService;
    this.messageService = messageService;
    this.memberRepository = memberRepository;
    this.reportRepository = reportRepository;
  }
  
  @PutMapping
  public void reportMember(@Valid @RequestBody ReportRequest request,
                           BindingResult bindingResult,
                           @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    if (bindingResult.hasErrors()) {
      log.debug("bindingResult: {}", bindingResult);
      throw new BadRequestException("invalid report request");
    }

    reportRepository.findByMeIdAndYouId(memberService.currentMemberId(), request.getMemberId())
        .ifPresent(report -> { throw new BadRequestException("already_reported", messageService.getMessage(MEMBER_ALREADY_REPORTED, lang));});

    Member you = memberRepository.findByIdAndDeletedAtIsNull(request.getMemberId())
        .orElseThrow(() -> new MemberNotFoundException(messageService.getMessage(MEMBER_NOT_FOUND, lang)));

    reportRepository.save(new Report(memberService.currentMember(), you, request.getReason()));
  }

  @GetMapping("/{id:.+}")
  public ReportResponse didReport(@PathVariable Integer id) {
    ReportResponse response = new ReportResponse(false);
    reportRepository.findByMeIdAndYouId(memberService.currentMemberId(), id)
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