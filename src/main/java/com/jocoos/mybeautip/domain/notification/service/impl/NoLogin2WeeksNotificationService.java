package com.jocoos.mybeautip.domain.notification.service.impl;

import com.jocoos.mybeautip.domain.notification.service.NotificationSendService;
import com.jocoos.mybeautip.domain.notification.service.NotificationService;
import com.jocoos.mybeautip.global.vo.Between;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.jocoos.mybeautip.domain.notification.code.TemplateType.NO_LOGIN_2WEEKS;

@Service
@RequiredArgsConstructor
public class NoLogin2WeeksNotificationService implements NotificationService<List<Member>> {

    private final MemberRepository memberRepository;
    private final NotificationSendService sendService;

    @Transactional
    public int occurs() {
        List<Between> noLoginNotificationDays = getNoLoginNotificationDays();
        List<Member> noLoginMembers = memberRepository.getMemberLastLoggedAtSameDayIn(noLoginNotificationDays);
        send(noLoginMembers);
        return noLoginMembers.size();
    }

    @Override
    public void send(List<Member> members) {
        List<Between> noLoginNotificationDays = getNoLoginNotificationDays();
        List<Member> noLoginMembers = memberRepository.getMemberLastLoggedAtSameDayIn(noLoginNotificationDays);
        List<Long> ids = members.stream()
                .map(Member::getId)
                .toList();
        sendService.send(NO_LOGIN_2WEEKS, ids, null, Map.of());
    }

    private List<Between> getNoLoginNotificationDays() {
        LocalDate before2Weeks = LocalDate.now().minusWeeks(2);
        LocalDate before4Weeks = LocalDate.now().minusWeeks(4);
        LocalDate before6Weeks = LocalDate.now().minusWeeks(6);

        return Stream.of(before2Weeks, before4Weeks, before6Weeks)
                .map(localDate -> Between.day(localDate, ZoneId.systemDefault()))
                .toList();
    }
}
