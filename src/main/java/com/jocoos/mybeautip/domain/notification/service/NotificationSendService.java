package com.jocoos.mybeautip.domain.notification.service;

import com.jocoos.mybeautip.domain.notification.client.AppPushService;
import com.jocoos.mybeautip.domain.notification.client.vo.AppPushMessage;
import com.jocoos.mybeautip.domain.notification.code.NotificationArgument;
import com.jocoos.mybeautip.domain.notification.code.NotificationStatus;
import com.jocoos.mybeautip.domain.notification.code.TemplateType;
import com.jocoos.mybeautip.domain.notification.converter.NotificationMessagePushConverter;
import com.jocoos.mybeautip.domain.notification.persistence.domain.NotificationCenterEntity;
import com.jocoos.mybeautip.domain.notification.persistence.domain.NotificationMessageCenterEntity;
import com.jocoos.mybeautip.domain.notification.persistence.domain.NotificationMessagePushEntity;
import com.jocoos.mybeautip.domain.notification.persistence.repository.NotificationMessagePushRepository;
import com.jocoos.mybeautip.domain.notification.service.dao.NotificationMessageCenterDao;
import com.jocoos.mybeautip.domain.notification.vo.NotificationTargetInfo;
import com.jocoos.mybeautip.global.util.StringConvertUtil;
import com.jocoos.mybeautip.support.RandomUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class NotificationSendService {

    private final NotificationMessageCenterDao messageCenterDao;
    private final NotificationMessagePushRepository messagePushRepository;
    private final MemberNotificationService memberNotificationService;
    private final NotificationMessagePushConverter pushConverter;
    private final AppPushService pushService;

    @Transactional
    public void sendAll(TemplateType templateType,
                     String imageUrl,
                     Map<String, String> additionalArguments) {
        int messageIndex = getMessageRandomIndex(templateType);
        List<NotificationTargetInfo> targetInfoList = memberNotificationService.getMemberNotificationInfo();
        for (NotificationTargetInfo targetInfo : targetInfoList) {
            send(templateType, imageUrl, additionalArguments, messageIndex, targetInfo);
        }
    }

    @Transactional
    public void send(TemplateType templateType,
                     List<Long> targetIds,
                     String imageUrl,
                     Map<String, String> additionalArguments) {
        int messageIndex = getMessageRandomIndex(templateType);
        List<NotificationTargetInfo> targetInfoList = memberNotificationService.getMemberNotificationInfo(targetIds);
        for (NotificationTargetInfo targetInfo : targetInfoList) {
            send(templateType, imageUrl, additionalArguments, messageIndex, targetInfo);
        }
    }

    @Transactional
    public void send(TemplateType templateType,
                     Long targetId,
                     String imageUrl,
                     Map<String, String> additionalArguments) {
        int messageIndex = getMessageRandomIndex(templateType);
        NotificationTargetInfo targetInfo = memberNotificationService.getMemberNotificationInfo(targetId);
        send(templateType, imageUrl, additionalArguments, messageIndex, targetInfo);
    }

    @Transactional
    public void forceSend(TemplateType templateType,
                          Long targetId,
                          String imageUrl,
                          Map<String, String> additionalArguments) {
        int messageIndex = getMessageRandomIndex(templateType);
        NotificationTargetInfo targetInfo = memberNotificationService.getMemberForceNotificationInfo(targetId);
        send(templateType, imageUrl, additionalArguments, messageIndex, targetInfo);
    }

    private void send(TemplateType templateType,
                      String imageUrl,
                      Map<String, String> additionalArguments,
                      int messageIndex,
                      NotificationTargetInfo targetInfo) {
        Map<String, String> arguments = getArgument(targetInfo.getNickname(), additionalArguments);
        NotificationCenterEntity notificationCenterEntity = sendCenter(messageIndex, templateType, imageUrl, targetInfo, arguments);
        sendAppPushMessage(messageIndex, targetInfo, templateType, imageUrl, notificationCenterEntity.getId(), arguments);
    }

    private NotificationCenterEntity sendCenter(int messageIndex,
                                                TemplateType templateType,
                                                String imageUrl,
                                                NotificationTargetInfo targetInfo,
                                                Map<String, String> arguments) {
        NotificationMessageCenterEntity messageInfo = getCenterMessage(messageIndex, templateType);
        NotificationCenterEntity entity = NotificationCenterEntity.builder()
                .userId(targetInfo.getMemberId())
                .status(NotificationStatus.NOT_READ)
                .arguments(StringConvertUtil.convertMapToJson(arguments))
                .imageUrl(imageUrl)
                .messageId(messageInfo.getId())
                .build();
        return messageCenterDao.saveNotificationSend(entity);
    }

    private void sendAppPushMessage(int index,
                                    NotificationTargetInfo targetInfo,
                                    TemplateType templateType,
                                    String imageUrl,
                                    Long notificationId,
                                    Map<String, String> arguments) {
        List<NotificationMessagePushEntity> entities = messagePushRepository.findByTemplateIdAndLastVersionIsTrue(templateType);
        NotificationMessagePushEntity entity = entities.get(index);
        AppPushMessage message = pushConverter.convert(entity, imageUrl, notificationId);
        AppPushMessage pushMessage = message.setArguments(arguments);
        pushService.send(targetInfo, pushMessage);
    }

    private int getMessageRandomIndex(TemplateType templateType) {
        int count = messageCenterDao.getMessageRandomIndex(templateType);
        return RandomUtils.getRandomIndex(count);
    }

    private NotificationMessageCenterEntity getCenterMessage(int index, TemplateType templateType) {
        List<NotificationMessageCenterEntity> entities = messageCenterDao.getLastVersion(templateType);
        return entities.get(index);
    }

    private Map<String, String> getArgument(String nickname, Map<String, String> additionalArguments) {
        Map<String, String> arguments = new HashMap<>();
        arguments.put(NotificationArgument.USER_NICKNAME.name(), nickname);
        arguments.putAll(additionalArguments);
        return arguments;
    }
}
