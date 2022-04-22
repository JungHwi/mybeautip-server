package com.jocoos.mybeautip.domain.notification.service;

import com.jocoos.mybeautip.devices.Device;
import com.jocoos.mybeautip.devices.DeviceRepository;
import com.jocoos.mybeautip.domain.notification.converter.MemberNotificationInfoConverter;
import com.jocoos.mybeautip.domain.notification.vo.NotificationTargetInfo;
import com.jocoos.mybeautip.exception.MemberNotFoundException;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberNotificationService {

    private final MemberRepository memberRepository;
    private final DeviceRepository deviceRepository;
    private final MemberNotificationInfoConverter converter;

    @Transactional(readOnly = true)
    public NotificationTargetInfo getMemberNotificationInfo(long userId) {
        Member member = getMember(userId);
        List<Device> devices = deviceRepository.findByCreatedByIdAndPushableAndValidAndCreatedByPushable(userId, true, true, true);
        return converter.convert(member, devices);
    }

    @Transactional(readOnly = true)
    public List<NotificationTargetInfo> getMemberNotificationInfo(List<Long> userIds) {
        List<Member> members = getMemberList(userIds);
        List<Device> devices = deviceRepository.findByCreatedByIdInAndPushableAndValidAndCreatedByPushable(userIds, true, true, true);

        return converter.convert(members, devices);
    }

    @Transactional(readOnly = true)
    public List<NotificationTargetInfo> getMemberNotificationInfo() {
        List<Member> members = memberRepository.findAll();
        List<Device> devices = deviceRepository.findByPushableAndValidAndCreatedByPushable(true, true, true);

        return converter.convert(members, devices);
    }

    private Member getMember(long userId) {
        return memberRepository.findById(userId)
                .orElseThrow(MemberNotFoundException::new);
    }

    private Map<Long, Member> getMemberMap(Set<Long> userIds) {
        List<Member> memberList = getMemberList(userIds);
        return memberList.stream()
                .collect(Collectors.toMap(Member::getId, Function.identity()));
    }

    private List<Member> getMemberList(Set<Long> userIds) {
        return memberRepository.findByIdIn(userIds);
    }

    private Map<Long, Member> getMemberMap(List<Long> userIds) {
        return getMemberMap(new HashSet<>(userIds));
    }

    private List<Member> getMemberList(List<Long> userIds) {
        return getMemberList(new HashSet<>(userIds));
    }
}
