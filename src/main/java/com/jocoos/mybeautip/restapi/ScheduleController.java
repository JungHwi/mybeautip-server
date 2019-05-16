package com.jocoos.mybeautip.restapi;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Strings;
import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.MemberNotFoundException;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.notification.MessageService;
import com.jocoos.mybeautip.recommendation.MemberRecommendation;
import com.jocoos.mybeautip.recommendation.MemberRecommendationRepository;
import com.jocoos.mybeautip.schedules.ScheduleRoughTime;
import com.jocoos.mybeautip.schedules.ScheduleService;
import com.jocoos.mybeautip.video.VideoRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.config.InstantNotificationConfig;
import com.jocoos.mybeautip.schedules.Schedule;

import javax.validation.constraints.NotNull;

import static com.jocoos.mybeautip.schedules.ScheduleService.*;

@Slf4j
@RestController
@RequestMapping(value = "/api/1")
public class ScheduleController {

  private final MemberService memberService;
  private final MessageService messageService;
  private final ScheduleService scheduleService;
  private final MemberRecommendationRepository memberRecommendationRepository;
  private final VideoRepository videoRepository;
  private final InstantNotificationConfig config;

  public ScheduleController(MemberService memberService,
                            MessageService messageService,
                            ScheduleService scheduleService,
                            MemberRecommendationRepository memberRecommendationRepository,
                            VideoRepository videoRepository,
                            InstantNotificationConfig config) {
    this.memberService = memberService;
    this.messageService = messageService;
    this.scheduleService = scheduleService;
    this.videoRepository = videoRepository;
    this.memberRecommendationRepository = memberRecommendationRepository;
    this.config = config;
  }

  @GetMapping("/schedules")
  public CursorResponse getSchedules(@RequestParam(defaultValue = "10") int count,
                                     @RequestParam(required = false, defaultValue = GO_FUTURE) String go,
                                     @RequestParam(required = false, defaultValue = "Asia/Seoul") String timeZone,
                                     @RequestParam(required = false, defaultValue = BASE_NOW) String base,
                                     @RequestParam(required = false) Long cursor) {
    Slice<Schedule> schedules = scheduleService.getSchedules(count, go, timeZone, base, cursor);

    // fetch all of recommended members
    List<MemberRecommendation> recommendedMembers = memberRecommendationRepository.findAll();

    ScheduleRoughTime srt = ScheduleRoughTime.now(config.getInterval());
    List<ScheduleInfo> result = schedules
            .stream()
            .map(s -> {
              ScheduleInfo scheduleInfo = new ScheduleInfo(s);
              // check if scheduler is recommended member
              recommendedMembers.forEach(rm -> {
                if (scheduleInfo.createdBy.equals(rm.getMember().getId())) {
                  scheduleInfo.recommended = true;
                }
              });

              // check if I(or guest) am following scheduler
              scheduleInfo.setFollowingId(memberService.getFollowingId(s.getCreatedBy()));

              // check if scheduled video exists
              if (srt.checkVideo(s.getStartedAt())) {
                // find the latest video of member at scheduled time
                videoRepository.findTopByMemberIdAndCreatedAtBetweenAndDeletedAtIsNullOrderByCreatedAtDesc(
                            s.getCreatedBy().getId(), srt.getAheadTime(), srt.getBehindTime())
                        .ifPresent(v -> {
                          scheduleInfo.videoId = v.getId();
                          scheduleInfo.videoState = v.getState();
                        });
              }

              return scheduleInfo;
            })
            .collect(Collectors.toList());

    String nextCursor = null;
    if (!CollectionUtils.isEmpty(result)) {
      nextCursor = String.valueOf(result.get(result.size() - 1).getStartedAt().getTime());
    }

    return new CursorResponse.Builder<>("/api/1/schedules", result)
            .withCount(count)
            .withCursor(nextCursor)
            .toBuild();
  }

  // member specific
  @GetMapping("/members/me/schedules")
  public CursorResponse getMySchedules(@RequestParam(defaultValue = "10") int count,
                                       @RequestParam(required = false) Long cursor) {
    PageRequest pageRequest = PageRequest.of(0, count, new Sort(Sort.Direction.ASC, "startedAt"));

    Date startedAt;
    if (cursor != null) {
      startedAt = new Date(cursor);
    } else {
      startedAt = new Date();
    }

    List<ScheduleInfo> result = scheduleService
            .getSchedulesByMember(memberService.currentMemberId(), startedAt, pageRequest)
            .stream()
            .map(ScheduleInfo::new)
            .collect(Collectors.toList());


    String nextCursor = null;
    if (!CollectionUtils.isEmpty(result)) {
      nextCursor = String.valueOf(result.get(result.size() - 1).getStartedAt().getTime());
    }

    return new CursorResponse.Builder<>("/api/1/members/me/schedules", result)
            .withCount(count)
            .withCursor(nextCursor)
            .toBuild();
  }

  @PostMapping("/members/me/schedules")
  public ResponseEntity createSchedule(@RequestBody CreateScheduleRequest request) {
    log.debug("{}", request);
    Member member = memberService.currentMember();
    if (member == null) {
      throw new MemberNotFoundException("member_not_found");
    }

    Schedule schedule = new Schedule();
    BeanUtils.copyProperties(request, schedule);

    schedule.setStartedAt(request.getStartedAt());
    schedule.setCreatedBy(member);
    schedule.setThumbnailUrl(member.getAvatarUrl());

    scheduleService.save(schedule);
    log.debug("{}", schedule);
    return new ResponseEntity<>(schedule, HttpStatus.OK);
  }

  @PatchMapping("/members/me/schedules/{id}")
  public ResponseEntity updateSchedule(@PathVariable Long id,
                                       @RequestBody UpdateScheduleRequest request,
                                       @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    if (request == null ||
            Strings.isNullOrEmpty(request.title) || request.startedAt == null) {
      throw new BadRequestException("invalid_values", "All values are null.");
    }

    Schedule schedule = scheduleService.updateSchedule(id, memberService.currentMemberId(), request, lang);
    return new ResponseEntity<>(schedule, HttpStatus.OK);
  }

  @DeleteMapping("/members/me/schedules/{id}")
  public ResponseEntity deleteSchedule(@PathVariable Long id,
                             @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    scheduleService.delete(id, memberService.currentMemberId(), lang);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Data
  public static class CreateScheduleRequest {
    @NotNull
    private String title;

    @NotNull
    private Date startedAt;
  }

  @Data
  public static class UpdateScheduleRequest {
    private String title;
    private Date startedAt;
  }

  @Data
  static class ScheduleInfo {
    private Long id;
    private String title;
    private String thumbnailUrl;
    private Long createdBy;
    private String username;
    private Date createdAt;
    private Date startedAt;
    private Date modifiedAt;
    private Date deletedAt;

    private Long followingId;
    private Long videoId;
    private String videoState;
    private Boolean recommended = false;

    public ScheduleInfo(Schedule s) {
      BeanUtils.copyProperties(s, this);
      this.createdBy = s.getCreatedBy().getId();
      this.username = s.getCreatedBy().getUsername();
    }
  }
}
