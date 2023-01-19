package com.jocoos.mybeautip.domain.notification.aspect.service;

import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityFile;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityDao;
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
import com.jocoos.mybeautip.domain.notification.vo.NotificationTargetInfo;
import com.jocoos.mybeautip.global.code.UrlDirectory;
import com.jocoos.mybeautip.global.util.ImageUrlConvertUtil;
import com.jocoos.mybeautip.global.util.StringConvertUtil;
import com.jocoos.mybeautip.support.RandomUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class CommunityNotificationSendHelper {

    private final CommunityDao communityDao;
    private final AppPushService pushService;
    private final MemberNotificationService memberNotificationService;
    private final NotificationMessagePushRepository messagePushRepository;
    private final NotificationCenterRepository notificationCenterRepository;
    private final NotificationMessageCenterRepository messageCenterRepository;
    private final NotificationMessagePushConverter pushConverter;


    public void sendNotification(Long communityId, TemplateType templateType) {
        int messageIndex = getMessageRandomIndex(templateType);
        Community community = communityDao.get(communityId);

        NotificationTargetInfo targetInfo = getTargetInfo(community.getMemberId());

        Map<String, String> arguments = getArgument(targetInfo.getNickname(), community);
        String thumbnailFile = community.getCommunityFileList().stream()
                .findFirst()
                .map(CommunityFile::getFile)
                .orElse(null);

        String thumbnailFileUrl = ImageUrlConvertUtil.toUrl(thumbnailFile, UrlDirectory.COMMUNITY, community.getId());

        NotificationCenterEntity notificationCenterEntity = sendCenter(messageIndex, templateType, thumbnailFile, targetInfo, arguments);
        sendAppPush(messageIndex, templateType, thumbnailFileUrl, notificationCenterEntity.getId(), targetInfo, arguments);
    }

    private int getMessageRandomIndex(TemplateType templateType) {
        int count = messageCenterRepository.countByTemplateIdAndLastVersionIsTrue(templateType);
        return RandomUtils.getRandomIndex(count);
    }

    private NotificationTargetInfo getTargetInfo(long memberId) {
        return memberNotificationService.getMemberNotificationInfo(memberId);
    }

    private Map<String, String> getArgument(String nickname, Community community) {
        Map<String, String> arguments = new HashMap<>();
        arguments.put(NotificationArgument.USER_NICKNAME.name(), nickname);
        arguments.put(NotificationArgument.COMMUNITY_ID.name(), String.valueOf(community.getId()));
        return arguments;
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

        return notificationCenterRepository.save(entity);
    }

    private NotificationMessageCenterEntity getCenterMessage(int index, TemplateType templateType) {
        List<NotificationMessageCenterEntity> entities = messageCenterRepository.findByTemplateIdAndLastVersionIsTrue(templateType);
        return entities.get(index);
    }

    private void sendAppPush(int messageIndex,
                             TemplateType templateType,
                             String imageUrl,
                             Long notificationId,
                             NotificationTargetInfo targetInfo,
                             Map<String, String> arguments) {
        AppPushMessage pushMessage = getAppPushMessage(messageIndex, templateType, imageUrl, notificationId, arguments);
        pushService.send(targetInfo, pushMessage);
    }

    private AppPushMessage getAppPushMessage(int index, TemplateType templateType, String imageUrl, Long notificationId, Map<String, String> arguments) {
        List<NotificationMessagePushEntity> entities = messagePushRepository.findByTemplateIdAndLastVersionIsTrue(templateType);
        NotificationMessagePushEntity entity = entities.get(index);
        AppPushMessage message = pushConverter.convert(entity, imageUrl, notificationId);
        return message.setArguments(arguments);
    }

}
