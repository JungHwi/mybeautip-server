package com.jocoos.mybeautip.notification;

import com.jocoos.mybeautip.config.InstantNotificationConfig;
import com.jocoos.mybeautip.devices.DeviceService;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.schedules.ScheduleService;
import com.jocoos.mybeautip.video.Video;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class InstantMessageService {
    private static final String INSTANT_VIDEO_START_TITLE = "instant.video_started_title";
    private static final String INSTANT_VIDEO_START_MESSAGE = "instant.video_started_message";

    private final DeviceService deviceService;
    private final MessageService messageService;
    private final InstantNotificationConfig config;
    private final ScheduleService scheduleService;

    @Qualifier("instantMessageTaskScheduler")
    private final ThreadPoolTaskScheduler taskScheduler;

    public InstantMessageService(DeviceService deviceService,
                                 MessageService messageService,
                                 InstantNotificationConfig instantNotificationConfig,
                                 ScheduleService scheduleService,
                                 ThreadPoolTaskScheduler taskScheduler) {
        this.deviceService = deviceService;
        this.messageService = messageService;
        this.config = instantNotificationConfig;
        this.scheduleService = scheduleService;
        this.taskScheduler = taskScheduler;
    }

    @Async
    public void instantPushMessage(Video video, List<Member> excludes) {
        if (!"BROADCASTED".equals(video.getType())) {
            return;
        }

        scheduleService.getSchedule(video)
                .ifPresent(s -> {
                            log.debug("{}", s);
                            String title = !StringUtils.isBlank(s.getInstantTitle()) ? s.getInstantTitle() : video.getTitle();
                            String message = !StringUtils.isBlank(s.getInstantMessage()) ? s.getInstantMessage() : getDefaultMessage(INSTANT_VIDEO_START_MESSAGE);
                            log.debug("title: {}, message: {}", title, message);

                            taskScheduler.schedule(new InstantNotificationTask(
                                            deviceService, video, message, title, excludes),
                                    new Date(System.currentTimeMillis() + config.getDelay()));
                        }
                );
    }

    private String getDefaultMessage(String code, String... args) {
        return messageService.getNotificationMessage(code, args);
    }
}
