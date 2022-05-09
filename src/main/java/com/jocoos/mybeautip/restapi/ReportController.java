package com.jocoos.mybeautip.restapi;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.member.block.BlockService;
import com.jocoos.mybeautip.member.report.Report;
import com.jocoos.mybeautip.member.report.ReportRepository;
import com.jocoos.mybeautip.notification.MessageService;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.VideoRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Slf4j
@RestController
@RequestMapping(value = "/api/1/members/me/reports", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReportController {

  private final BlockService blockService;
  private final MemberService memberService;
  private final MessageService messageService;
  private final ReportRepository reportRepository;
  private final VideoRepository videoRepository;

  private static final String MEMBER_NOT_FOUND = "member.not_found";
  private static final String MEMBER_ALREADY_REPORTED = "member.already_reported";

  public ReportController(MemberService memberService,
                          MessageService messageService,
                          BlockService blockService,
                          ReportRepository reportRepository,
                          VideoRepository videoRepository) {
    this.memberService = memberService;
    this.messageService = messageService;
    this.blockService = blockService;
    this.reportRepository = reportRepository;
    this.videoRepository = videoRepository;
  }
  
  @PutMapping
  public ReportResponse reportMember(@Valid @RequestBody ReportRequest request,
                           @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    int reasonCode = (request.getReasonCode() == null ? 0 : request.getReasonCode());
    
    Video video = null;
    if (request.getVideoId() != null) {
      video = videoRepository.findById(request.getVideoId()).orElse(null);
    }
    
    if (reportRepository.findByMeIdAndYouId(memberService.currentMemberId(), request.getMemberId()).isPresent()) {
      throw new BadRequestException("already_reported", messageService.getMessage(MEMBER_ALREADY_REPORTED, lang));
    }
    Member member = memberService.currentMember();
    
    Report report = memberService.reportMember(member, request.getMemberId(), reasonCode,
        request.getReason(), video, lang);

    return new ReportResponse(report.getId());
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
    Long id;
    Boolean reported;

    ReportResponse(Long id) {
      this.id = id;
    }

    ReportResponse(Boolean reported) {
      this.reported = reported;
    }
  }
}