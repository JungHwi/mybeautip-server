package com.jocoos.mybeautip.admin;

import com.jocoos.mybeautip.exception.MemberNotFoundException;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.schedules.Schedule;
import com.jocoos.mybeautip.schedules.ScheduleRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@Slf4j
@RestController
@RequestMapping("/api/admin/manual/schedules")
public class AdminScheduleController extends AdminDateController {

    private final ScheduleRepository scheduleRepository;
    private final MemberRepository memberRepository;

    public AdminScheduleController(ScheduleRepository scheduleRepository,
                                   MemberRepository memberRepository) {
        this.scheduleRepository = scheduleRepository;
        this.memberRepository = memberRepository;
    }

    @PostMapping
    public ResponseEntity createSchedule(@RequestBody CreateScheduleRequest request) {
        log.debug("{}", request);
        Schedule schedule = new Schedule();
        BeanUtils.copyProperties(request, schedule);

        schedule.setStartedAt(getUTCDate(request.getStartedAt()));
        schedule.setCreatedBy(memberRepository.findById(request.getMemberId()).orElseThrow(() -> new MemberNotFoundException("member_not_found")));

        scheduleRepository.save(schedule);
        log.debug("{}", schedule);
        return new ResponseEntity<>(schedule, HttpStatus.OK);
    }

    @Data
    public static class CreateScheduleRequest {
        @NotNull
        private String title;

        @NotNull
        private String thumbnailUrl;

        @NotNull
        private String startedAt;

        @NotNull
        private Long memberId;
        private String instantTitle;
        private String instantMessage;
    }
}
