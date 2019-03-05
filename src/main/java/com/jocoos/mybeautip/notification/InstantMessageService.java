package com.jocoos.mybeautip.notification;

import java.util.Date;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.config.InstantNotificationConfig;
import com.jocoos.mybeautip.devices.DeviceService;
import com.jocoos.mybeautip.video.Video;

@Slf4j
@Service
public class InstantMessageService {
  private static final String INSTANT_VIDEO_START_TITLE = "instant.video_started_title";
  private static final String INSTANT_VIDEO_START_MESSAGE = "instant.video_started_message";

  private final DeviceService deviceService;
  private final MessageService messageService;
  private final InstantNotificationConfig instantNotificationConfig;

  @Qualifier("instantMessageTaskScheduler")
  private final ThreadPoolTaskScheduler taskScheduler;

  public InstantMessageService(DeviceService deviceService,
                               MessageService messageService,
                               InstantNotificationConfig instantNotificationConfig,
                               ThreadPoolTaskScheduler taskScheduler) {
    this.deviceService = deviceService;
    this.messageService = messageService;
    this.taskScheduler = taskScheduler;
    this.instantNotificationConfig = instantNotificationConfig;
  }

  @Async
  public void instantPushMessage(Video video) {
    taskScheduler.schedule(new InstantNotificationTask(
       deviceService, video, getDefaultMessage(INSTANT_VIDEO_START_MESSAGE, null), video.getTitle()),
       new Date(System.currentTimeMillis() + instantNotificationConfig.getDelay()));
  }

  private String getDefaultMessage(String code, String ...args) {
    return messageService.getNotificationMessage(code, args);
  }
}
