package com.jocoos.mybeautip.domain.notification.service.impl;

import com.jocoos.mybeautip.global.vo.Day;
import com.jocoos.mybeautip.domain.notification.client.AppPushService;
import com.jocoos.mybeautip.domain.notification.client.vo.AppPushMessage;
import com.jocoos.mybeautip.domain.notification.code.NotificationArgument;
import com.jocoos.mybeautip.domain.notification.code.NotificationStatus;
import com.jocoos.mybeautip.domain.notification.code.TemplateType;
import com.jocoos.mybeautip.domain.notification.converter.NotificationMessagePushConverter;
import com.jocoos.mybeautip.domain.notification.persistence.domain.NotificationCenterEntity;
import com.jocoos.mybeautip.domain.notification.persistence.domain.NotificationMessageCenterEntity;
import com.jocoos.mybeautip.domain.notification.persistence.domain.NotificationMessagePushEntity;
import com.jocoos.mybeautip.domain.notification.persistence.repository.NotificationCenterRepository;
import com.jocoos.mybeautip.domain.notification.persistence.repository.NotificationMessageCenterRepository;
import com.jocoos.mybeautip.domain.notification.persistence.repository.NotificationMessagePushRepository;
import com.jocoos.mybeautip.domain.notification.service.MemberNotificationService;
import com.jocoos.mybeautip.domain.notification.service.NotificationService;
import com.jocoos.mybeautip.domain.notification.vo.NotificationTargetInfo;
import com.jocoos.mybeautip.global.util.StringConvertUtil;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.support.RandomUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class NoLogin2WeeksNotificationService implements NotificationService<List<Member>> {

    private final MemberRepository memberRepository;
    private final MemberNotificationService memberNotificationService;
    private final NotificationCenterRepository notificationCenterRepository;
    private final NotificationMessageCenterRepository messageCenterRepository;
    private final NotificationMessagePushRepository messagePushRepository;

    private final NotificationMessagePushConverter pushConverter;

    private final AppPushService pushService;

    private final TemplateType templateType = TemplateType.NO_LOGIN_2WEEKS;

    @Transactional
    public void occurs() {
        List<Day> noLoginNotificationDays = getNoLoginNotificationDays();
        List<Member> noLoginMembers = memberRepository.getMemberLastLoggedAtSameDayIn(noLoginNotificationDays);
        send(noLoginMembers);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void send(List<Member> members) {
        int messageIndex = getMessageRandomIndex();
        List<Long> ids = members.stream()
                .map(Member::getId)
                .collect(Collectors.toList());

        List<NotificationTargetInfo> targetInfoList = getTargetInfo(ids);

        for (NotificationTargetInfo targetInfo : targetInfoList) {
            Map<String, String> arguments = getArgument(targetInfo.getNickname());
            NotificationCenterEntity notificationCenterEntity = sendCenter(messageIndex, targetInfo, arguments);
            sendAppPush(messageIndex, notificationCenterEntity.getId(), targetInfo, arguments);
        }
    }

    private List<Day> getNoLoginNotificationDays() {
        LocalDate before2Weeks = LocalDate.now().minusWeeks(2);
        LocalDate before4Weeks = LocalDate.now().minusWeeks(4);
        LocalDate before6Weeks = LocalDate.now().minusWeeks(6);

        return Stream.of(before2Weeks, before4Weeks, before6Weeks)
                .map(localDate -> new Day(localDate, ZoneId.systemDefault()))
                .toList();
    }

    private NotificationCenterEntity sendCenter(int messageIndex, NotificationTargetInfo targetInfo, Map<String, String> arguments) {
        NotificationMessageCenterEntity messageInfo = getCenterMessage(messageIndex);
        NotificationCenterEntity entity = NotificationCenterEntity.builder()
                .userId(targetInfo.getMemberId())
                .status(NotificationStatus.NOT_READ)
                .arguments(StringConvertUtil.convertMapToJson(arguments))
                .messageId(messageInfo.getId())
                .build();

        return notificationCenterRepository.save(entity);
    }

    private void sendAppPush(int messageIndex, Long notificationId, NotificationTargetInfo targetInfo, Map<String, String> arguments) {
        AppPushMessage pushMessage = getAppPushMessage(messageIndex, notificationId, arguments);
        pushService.send(targetInfo, pushMessage);
    }

    private int getMessageRandomIndex() {
        int count = messageCenterRepository.countByTemplateIdAndLastVersionIsTrue(templateType);
        return RandomUtils.getRandomIndex(count);
    }

    private NotificationMessageCenterEntity getCenterMessage(int index) {
        List<NotificationMessageCenterEntity> entities = messageCenterRepository.findByTemplateIdAndLastVersionIsTrue(templateType);
        return entities.get(index);
    }

    private AppPushMessage getAppPushMessage(int index, Long notificationId, Map<String, String> arguments) {
        List<NotificationMessagePushEntity> entities = messagePushRepository.findByTemplateIdAndLastVersionIsTrue(templateType);
        NotificationMessagePushEntity entity = entities.get(index);
        AppPushMessage message = pushConverter.convert(entity, notificationId);
        return message.setArguments(arguments);
    }

    private List<NotificationTargetInfo> getTargetInfo(List<Long> ids) {
        return memberNotificationService.getMemberNotificationInfo(ids);
    }

    private Map<String, String> getArgument(String nickname) {
        Map<String, String> arguments = new HashMap<>();
        arguments.put(NotificationArgument.USER_NICKNAME.name(), nickname);
        return arguments;
    }
}
