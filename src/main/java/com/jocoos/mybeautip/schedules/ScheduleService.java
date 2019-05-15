package com.jocoos.mybeautip.schedules;

import com.jocoos.mybeautip.config.InstantNotificationConfig;
import com.jocoos.mybeautip.video.Video;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final InstantNotificationConfig config;

    public ScheduleService(ScheduleRepository scheduleRepository,
                           InstantNotificationConfig instantNotificationConfig) {
        this.scheduleRepository = scheduleRepository;
        this.config = instantNotificationConfig;
    }

    public Optional<Schedule> getSchedule(Video video) {
        ScheduleRoughTime roughTime = ScheduleRoughTime.now(config.getInterval());
        return scheduleRepository.findTopByCreatedByIdAndStartedAtBetweenAndDeletedAtIsNull
                (video.getMember().getId(), roughTime.getAheadTime(), roughTime.getBehindTime());
    }
}
