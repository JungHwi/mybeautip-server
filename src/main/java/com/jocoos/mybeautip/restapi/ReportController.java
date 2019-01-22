package com.jocoos.mybeautip.restapi;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.member.report.ReportRepository;
import com.jocoos.mybeautip.notification.MessageService;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.VideoRepository;

@Slf4j
@RestController
@RequestMapping(value = "/api/1/members/me/reports", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReportController {
  private final MemberService memberService;
  private final MessageService messageService;
  private final ReportRepository reportRepository;
  private final VideoRepository videoRepository;

  private static final String MEMBER_NOT_FOUND = "member.not_found";
  private static final String MEMBER_ALREADY_REPORTED = "member.already_reported";

  public ReportController(MemberService memberService,
                          MessageService messageService,
                          ReportRepository reportRepository,
                          VideoRepository videoRepository) {
    this.memberService = memberService;
    this.messageService = messageService;
    this.reportRepository = reportRepository;
    this.videoRepository = videoRepository;
  }
  
  @PutMapping
  public void reportMember(@Valid @RequestBody ReportRequest request,
                           @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    int reasonCode = (request.getReasonCode() == null ? 0 : request.getReasonCode());
    
    Video video = null;
    if (request.getVideoId() != null) {
      video = videoRepository.findById(request.getVideoId()).orElse(null);
    }
    
    if (reportRepository.findByMeIdAndYouId(memberService.currentMemberId(), request.getMemberId()).isPresent()) {
      throw new BadRequestException("already_reported", messageService.getMessage(MEMBER_ALREADY_REPORTED, lang));
    }
    
    memberService.reportMember(memberService.currentMember(), request.getMemberId(), reasonCode,
        request.getReason(), video, lang);
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
    
    private Integer reasonCode;
    
    private Long videoId;
  }

  @Data
  @AllArgsConstructor
  class ReportResponse {
    Boolean reported;
  }
}