package com.jocoos.mybeautip.domain.member.service;

import com.jocoos.mybeautip.domain.member.service.dao.MemberDao;
import com.jocoos.mybeautip.domain.notification.client.AppPushService;
import com.jocoos.mybeautip.domain.notification.client.vo.AppPushMessage;
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
import com.jocoos.mybeautip.domain.notification.vo.NotificationTargetInfo;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DormantMemberNotificationService {

    private final MemberDao memberDao;
    private final AppPushService pushService;
    private final MemberNotificationService memberNotificationService;
    private final NotificationCenterRepository notificationCenterRepository;
    private final NotificationMessagePushRepository messagePushRepository;
    private final NotificationMessageCenterRepository messageCenterRepository;
    private final NotificationMessagePushConverter pushConverter;

    private final TemplateType templateType = TemplateType.DORMANT_MEMBER;

    public int send() {
        int messageIndex = 0;
        List<Member> memberList = memberDao.getDormantNotificationTarget();
        List<Long> ids = memberList.stream()
                .map(Member::getId)
                .toList();

        List<NotificationTargetInfo> targetInfoList = this.getTargetInfo(ids);

        for (NotificationTargetInfo targetInfo : targetInfoList) {
            NotificationCenterEntity notificationCenterEntity = sendCenter(messageIndex, targetInfo);
            sendAppPush(messageIndex, notificationCenterEntity.getId(), targetInfo);
        }

        return memberList.size();
    }

    private List<NotificationTargetInfo> getTargetInfo(List<Long> ids) {
        return memberNotificationService.getMemberNotificationInfo(ids);
    }

    private void sendAppPush(int messageIndex, Long notificationId, NotificationTargetInfo targetInfo) {
        AppPushMessage pushMessage = getAppPushMessage(messageIndex, notificationId);
        pushService.send(targetInfo, pushMessage);
    }

    private NotificationCenterEntity sendCenter(int messageIndex, NotificationTargetInfo targetInfo) {
        NotificationMessageCenterEntity messageInfo = getCenterMessage(messageIndex);
        NotificationCenterEntity entity = NotificationCenterEntity.builder()
                .userId(targetInfo.getMemberId())
                .status(NotificationStatus.NOT_READ)
                .messageId(messageInfo.getId())
                .build();

        return notificationCenterRepository.save(entity);
    }

    private AppPushMessage getAppPushMessage(int messageIndex, Long notificationId) {
        List<NotificationMessagePushEntity> entities = messagePushRepository.findByTemplateIdAndLastVersionIsTrue(templateType);
        NotificationMessagePushEntity entity = entities.get(messageIndex);
        AppPushMessage message = pushConverter.convert(entity, notificationId);
        return message;
    }

    private NotificationMessageCenterEntity getCenterMessage(int index) {
        List<NotificationMessageCenterEntity> entities = messageCenterRepository.findByTemplateIdAndLastVersionIsTrue(templateType);
        return entities.get(index);
    }
}
