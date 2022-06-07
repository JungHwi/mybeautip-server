package com.jocoos.mybeautip.domain.notification.service.impl;

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
import com.jocoos.mybeautip.support.RandomUtils;
import com.jocoos.mybeautip.video.Video;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class VideoUploadNotificationService implements NotificationService<Video> {

    private final MemberNotificationService memberNotificationService;
    private final NotificationCenterRepository notificationCenterRepository;
    private final NotificationMessageCenterRepository messageCenterRepository;
    private final NotificationMessagePushRepository messagePushRepository;

    private final NotificationMessagePushConverter pushConverter;

    private final AppPushService pushService;

    private final TemplateType templateType = TemplateType.VIDEO_UPLOAD;

    @AfterReturning(returning = "result", value = "execution(* com.jocoos.mybeautip.restapi.CallbackController.startVideo(..))")
    public void occurs(Object result) {
        if (result instanceof Video) {
            Video video = (Video) result;
            if (verify(video)) {
                send(video);
            }
        } else {
            log.error("Must be Video. But this object is > " + result);
        }
    }

    private boolean verify(Video video) {
        return "UPLOADED".equals(video.getType()) && "VOD".equals(video.getState());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void send(Video video) {
        int messageIndex = getMessageRandomIndex();
        List<NotificationTargetInfo> targetInfoList = getTargetInfo();

        for (NotificationTargetInfo targetInfo : targetInfoList) {
            Map<String, String> arguments = getArgument(targetInfo.getNickname(), video);
            NotificationCenterEntity notificationCenterEntity = sendCenter(messageIndex, video.getThumbnailUrl(), targetInfo, arguments);
            sendAppPush(messageIndex, video.getThumbnailUrl(), notificationCenterEntity.getId(), targetInfo, arguments);
        }
    }

    private NotificationCenterEntity sendCenter(int messageIndex, String imageUrl, NotificationTargetInfo targetInfo, Map<String, String> arguments) {
        NotificationMessageCenterEntity messageInfo = getCenterMessage(messageIndex);
        NotificationCenterEntity entity = NotificationCenterEntity.builder()
                .userId(targetInfo.getMemberId())
                .status(NotificationStatus.NOT_READ)
                .arguments(StringConvertUtil.convertMapToJson(arguments))
                .imageUrl(imageUrl)
                .messageId(messageInfo.getId())
                .build();

        return notificationCenterRepository.save(entity);
    }

    private void sendAppPush(int messageIndex, String imageUrl, Long notificationId, NotificationTargetInfo targetInfo, Map<String, String> arguments) {
        AppPushMessage pushMessage = getAppPushMessage(messageIndex, imageUrl, notificationId, arguments);
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

    private AppPushMessage getAppPushMessage(int index, String imageUrl, Long notificationId, Map<String, String> arguments) {
        List<NotificationMessagePushEntity> entities = messagePushRepository.findByTemplateIdAndLastVersionIsTrue(templateType);
        NotificationMessagePushEntity entity = entities.get(index);
        AppPushMessage message = pushConverter.convert(entity, imageUrl, notificationId);
        return message.setArguments(arguments);
    }

    private List<NotificationTargetInfo> getTargetInfo() {
        return memberNotificationService.getMemberNotificationInfo();
    }

    private Map<String, String> getArgument(String nickname, Video video) {
        Map<String, String> arguments = new HashMap<>();
        arguments.put(NotificationArgument.USER_NICKNAME.name(), nickname);
        arguments.put(NotificationArgument.VIDEO_ID.name(), String.valueOf(video.getId()));
        return arguments;
    }
}
