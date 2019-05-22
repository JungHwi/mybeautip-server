package com.jocoos.mybeautip.schedules;

import com.jocoos.mybeautip.config.InstantNotificationConfig;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.notification.MessageService;
import com.jocoos.mybeautip.restapi.ScheduleController;
import com.jocoos.mybeautip.video.Video;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
public class ScheduleService {
    private static final String SCHEDULE_ITEM_NOT_FOUND = "schedule.item_not_found";

    public static final String DIRECTION_PREV = "prev";
    public static final String DIRECTION_NEXT = "next";

    private final MessageService messageService;
    private final ScheduleRepository scheduleRepository;
    private final InstantNotificationConfig config;

    public ScheduleService(MessageService messageService,
                           ScheduleRepository scheduleRepository,
                           InstantNotificationConfig instantNotificationConfig) {
        this.messageService = messageService;
        this.scheduleRepository = scheduleRepository;
        this.config = instantNotificationConfig;
    }

    public Page<Schedule> getSchedules(Date now, Pageable pageable) {
        return scheduleRepository.findByStartedAtAfterAndDeletedAtIsNull(now, pageable);
    }

    public Slice<Schedule> getSchedules(int count, String direction, boolean includeCursor, Long cursor) {
        Date startCursor;
        if (cursor != null) {
            if (includeCursor) {
                if (DIRECTION_PREV.equals(direction)) {
                    cursor += 1;
                } else {
                    cursor -= 1;
                }
            }
            startCursor = new Date(cursor);
        } else {
            startCursor = getFutureDate();
        }

        Page<Schedule> schedules;
        if (DIRECTION_PREV.equals(direction)) {
            PageRequest pageRequest = PageRequest.of(0, count, new Sort(Sort.Direction.DESC, "startedAt"));
            schedules = scheduleRepository.findByStartedAtBeforeAndDeletedAtIsNull(startCursor, pageRequest);
        } else {
            PageRequest pageRequest = PageRequest.of(0, count, new Sort(Sort.Direction.ASC, "startedAt"));
            schedules = scheduleRepository.findByStartedAtAfterAndDeletedAtIsNull(startCursor, pageRequest);
        }
        return schedules;
    }

    public Page<Schedule> getSchedulesByMember(Long memberId, int count, Long cursor) {
        Date startCursor;
        if (cursor != null) {
            startCursor = new Date(cursor);
        } else {
            startCursor = getFutureDate();
        }
        PageRequest pageRequest = PageRequest.of(0, count, new Sort(Sort.Direction.DESC, "startedAt"));
        return scheduleRepository
                .findByCreatedByIdAndStartedAtBeforeAndDeletedAtIsNull(memberId, startCursor, pageRequest);
    }

    private Date getFutureDate() {
        // to be used as a start cursor
        Instant instant = Instant.now().plus(36500, ChronoUnit.DAYS); // 100 years after
        return Date.from(instant);
    }

    public Optional<Schedule> getSchedule(Video video) {
        ScheduleRoughTime roughTime = ScheduleRoughTime.now(config.getInterval());
        return scheduleRepository.findTopByCreatedByIdAndStartedAtBetweenAndDeletedAtIsNull
                (video.getMember().getId(), roughTime.getAheadTime(), roughTime.getBehindTime());
    }

    @Transactional
    public Schedule updateSchedule(Long id, Long memberId,
                                             ScheduleController.UpdateScheduleRequest request,
                                             String lang) {
        Schedule schedule = scheduleRepository.findByIdAndCreatedById(id, memberId)
                .orElseThrow(() -> new NotFoundException("schedule_item_not_found",
                        messageService.getMessage(SCHEDULE_ITEM_NOT_FOUND, lang)));

        if (request.getTitle() != null) {
            schedule.setTitle(request.getTitle());
        }
        if (request.getStartedAt() != null) {
            schedule.setStartedAt(request.getStartedAt());
        }
        scheduleRepository.save(schedule);
        return schedule;
    }

    public Schedule save(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    @Transactional
    public Optional<Object> delete(Long id, Long memberId, String lang) {
        return scheduleRepository.findByIdAndCreatedById(id, memberId)
                .map(s -> {
                    scheduleRepository.delete(s);
                    return Optional.empty();
                })
                .orElseThrow(() -> new NotFoundException("schedule_item_not_found",
                        messageService.getMessage(SCHEDULE_ITEM_NOT_FOUND, lang)));
    }

    public ScheduleRoughTime checkRoughTimeByNow() {
        return ScheduleRoughTime.now(config.getInterval());
    }
}
