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
import com.jocoos.mybeautip.post.Post;
import com.jocoos.mybeautip.post.PostLike;
import com.jocoos.mybeautip.post.PostLikeRepository;
import com.jocoos.mybeautip.post.PostRepository;
import com.jocoos.mybeautip.support.RandomUtils;
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

import static com.jocoos.mybeautip.global.constant.SignConstant.ONE;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class CommunityLikeFirstNotificationService implements NotificationService<PostLike> {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final MemberNotificationService memberNotificationService;
    private final NotificationCenterRepository notificationCenterRepository;
    private final NotificationMessageCenterRepository messageCenterRepository;
    private final NotificationMessagePushRepository messagePushRepository;

    private final NotificationMessagePushConverter pushConverter;

    private final AppPushService pushService;

    private final TemplateType templateType = TemplateType.COMMUNITY_LIKE_1;

    @AfterReturning(returning = "result", value = "execution(* com.jocoos.mybeautip.post.PostService.likePost(..))")
    public void occurs(Object result) {
        if (result instanceof PostLike) {
            PostLike postLike = (PostLike) result;
            if (verify(postLike)) {
                send(postLike);
            }
        } else {
            log.error("Must be PostLike. But this object is > " + result);
        }
    }

    private boolean verify(PostLike postLike) {
        int countLike = postLikeRepository.countByPostId(postLike.getPost().getId());
        return countLike == ONE && postLike.getCreatedAt().equals(postLike.getModifiedAt());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void send(PostLike postLike) {
        int messageIndex = getMessageRandomIndex();
        Post post = postRepository.getById(postLike.getPost().getId());
        NotificationTargetInfo targetInfo = getTargetInfo(post.getCreatedBy().getId());

        Map<String, String> arguments = getArgument(targetInfo.getNickname(), post);
        sendCenter(messageIndex, post.getThumbnailUrl(), targetInfo, arguments);
        sendAppPush(messageIndex, post.getThumbnailUrl(), targetInfo, arguments);
    }

    private void sendCenter(int messageIndex, String imageUrl, NotificationTargetInfo targetInfo, Map<String, String> arguments) {
        NotificationMessageCenterEntity messageInfo = getCenterMessage(messageIndex);
        NotificationCenterEntity entity = NotificationCenterEntity.builder()
                .userId(targetInfo.getMemberId())
                .status(NotificationStatus.NOT_READ)
                .arguments(StringConvertUtil.convertMapToJson(arguments))
                .imageUrl(imageUrl)
                .messageId(messageInfo.getId())
                .build();

        notificationCenterRepository.save(entity);
    }

    private void sendAppPush(int messageIndex, String imageUrl, NotificationTargetInfo targetInfo, Map<String, String> arguments) {
        AppPushMessage pushMessage = getAppPushMessage(messageIndex, imageUrl, arguments);
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

    private AppPushMessage getAppPushMessage(int index, String imageUrl, Map<String, String> arguments) {
        List<NotificationMessagePushEntity> entities = messagePushRepository.findByTemplateIdAndLastVersionIsTrue(templateType);
        NotificationMessagePushEntity entity = entities.get(index);
        AppPushMessage message = pushConverter.convert(entity, imageUrl);
        return message.setArguments(arguments);
    }

    private NotificationTargetInfo getTargetInfo(long memberId) {
        return memberNotificationService.getMemberNotificationInfo(memberId);
    }

    private Map<String, String> getArgument(String nickname, Post post) {
        Map<String, String> arguments = new HashMap<>();
        arguments.put(NotificationArgument.USER_NICKNAME.name(), nickname);
        arguments.put(NotificationArgument.POST_ID.name(), String.valueOf(post.getId()));
        return arguments;
    }
}
