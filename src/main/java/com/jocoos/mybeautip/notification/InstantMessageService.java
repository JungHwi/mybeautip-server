package com.jocoos.mybeautip.notification;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.config.InstantNotificationConfig;
import com.jocoos.mybeautip.devices.DeviceService;
import com.jocoos.mybeautip.schedules.ScheduleRepository;
import com.jocoos.mybeautip.video.Video;

@Slf4j
@Service
public class InstantMessageService {
  private static final String INSTANT_VIDEO_START_TITLE = "instant.video_started_title";
  private static final String INSTANT_VIDEO_START_MESSAGE = "instant.video_started_message";

  private final DeviceService deviceService;
  private final MessageService messageService;
  private final InstantNotificationConfig config;
  private final ScheduleRepository scheduleRepository;

  @Qualifier("instantMessageTaskScheduler")
  private final ThreadPoolTaskScheduler taskScheduler;

  public InstantMessageService(DeviceService deviceService,
                               MessageService messageService,
                               InstantNotificationConfig instantNotificationConfig,
                               ScheduleRepository scheduleRepository,
                               ThreadPoolTaskScheduler taskScheduler) {
    this.deviceService = deviceService;
    this.messageService = messageService;
    this.config = instantNotificationConfig;
    this.scheduleRepository = scheduleRepository;
    this.taskScheduler = taskScheduler;
  }

  @Async
  public void instantPushMessage(Video video) {
    Instant instant = Instant.now().minus(config.getInterval(), ChronoUnit.MINUTES);
    Date now = Date.from(instant);

    scheduleRepository.findByCreatedByIdAndStartedAtAfterAndDeletedAtIsNull(video.getMember().getId(), now)
      .ifPresent(s -> {
         log.debug("{}", s);
         String title = !Strings.isNullOrEmpty(s.getInstantTitle()) ? s.getInstantTitle() : video.getTitle();
         String message = !Strings.isNullOrEmpty(s.getInstantMessage()) ? s.getInstantMessage() : getDefaultMessage(INSTANT_VIDEO_START_MESSAGE, null);
         log.debug("title: {}, message: {}", title, message);

         taskScheduler.schedule(new InstantNotificationTask(
               deviceService, video, message, title),
            new Date(System.currentTimeMillis() + config.getDelay()));
       }
    );
  }

  private String getDefaultMessage(String code, String ...args) {
    return messageService.getNotificationMessage(code, args);
  }
}
