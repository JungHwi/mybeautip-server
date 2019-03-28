package com.jocoos.mybeautip.restapi;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.config.InstantNotificationConfig;
import com.jocoos.mybeautip.member.MemberInfo;
import com.jocoos.mybeautip.schedules.Schedule;
import com.jocoos.mybeautip.schedules.ScheduleRepository;

@Slf4j
@RestController
@RequestMapping(value = "/api/1/schedules")
public class ScheduleController {

  private final ScheduleRepository scheduleRepository;
  private final InstantNotificationConfig config;

  public ScheduleController(ScheduleRepository scheduleRepository,
                            InstantNotificationConfig config) {
    this.scheduleRepository = scheduleRepository;
    this.config = config;
  }

  @GetMapping
  public ResponseEntity<List<ScheduleInfo>> getSchduels(@RequestParam(defaultValue = "10") int count) {
    PageRequest pageRequest = PageRequest.of(0, count, new Sort(Sort.Direction.ASC, "startedAt"));

    Instant instant = Instant.now().minus(config.getInterval(), ChronoUnit.MINUTES);
    Date now = Date.from(instant);

    List<ScheduleInfo> result = scheduleRepository.findByStartedAtAfterAndDeletedAtIsNull(now, pageRequest)
       .stream()
       .map(ScheduleInfo::new)
       .collect(Collectors.toList());

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @Data
  static class ScheduleInfo {
    private Long id;
    private String title;
    private String thumbnailUrl;
    private Long createdBy;
    private Date createdAt;
    private Date startedAt;
    private Date modifiedAt;
    private Date deletedAt;
    private MemberInfo member;
    private String instantTitle;
    private String instantMessage;

    public ScheduleInfo(Schedule s) {
      BeanUtils.copyProperties(s, this);
      this.createdBy = s.getCreatedBy().getId();
      this.member = new MemberInfo((s.getCreatedBy()));
    }
  }
}