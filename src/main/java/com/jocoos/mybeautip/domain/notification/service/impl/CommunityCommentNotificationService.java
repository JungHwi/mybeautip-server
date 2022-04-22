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
import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.global.util.StringConvertUtil;
import com.jocoos.mybeautip.member.comment.Comment;
import com.jocoos.mybeautip.post.Post;
import com.jocoos.mybeautip.post.PostRepository;
import com.jocoos.mybeautip.support.RandomUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class CommunityCommentNotificationService implements NotificationService<Comment> {

    private final PostRepository postRepository;
    private final MemberNotificationService memberNotificationService;
    private final NotificationCenterRepository notificationCenterRepository;
    private final NotificationMessageCenterRepository messageCenterRepository;
    private final NotificationMessagePushRepository messagePushRepository;

    private final NotificationMessagePushConverter pushConverter;

    private final AppPushService pushService;

    private final TemplateType templateType = TemplateType.COMMUNITY_COMMENT;

    @AfterReturning(returning = "result", value = "execution(* com.jocoos.mybeautip.member.comment.CommentService.addComment(..))")
    public void occurs(Object result) {
        if (result instanceof Comment) {
            Comment comment = (Comment) result;
            if (verify(comment)) {
                send(comment);
            }
        } else {
            log.error("Must be Comment. But this object is > " + result);
        }
    }

    private boolean verify(Comment comment) {
        return comment.getPostId() != null &&
                comment.getPostId() > 0 &&
                comment.getParentId() == null;
    }

    @Override
    public void send(Comment comment) {
        int messageIndex = getMessageRandomIndex();
        Post post = postRepository.findById(comment.getPostId())
                .orElseThrow(() -> new BadRequestException("No such Post."));

//      NotificationTargetInfo targetInfo = getTargetInfo(post.getCreatedBy().getId());
        NotificationTargetInfo targetInfo = getTargetInfo(4L);

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
        int count = messageCenterRepository.countByTemplateIdAndIsLastVersionIsTrue(templateType);
        return RandomUtils.getRandomIndex(count);
    }

    private NotificationMessageCenterEntity getCenterMessage(int index) {
        List<NotificationMessageCenterEntity> entities = messageCenterRepository.findByTemplateIdAndIsLastVersionIsTrue(templateType);
        return entities.get(index);
    }

    private AppPushMessage getAppPushMessage(int index, String imageUrl, Map<String, String> arguments) {
        List<NotificationMessagePushEntity> entities = messagePushRepository.findByTemplateIdAndIsLastVersionIsTrue(templateType);
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
