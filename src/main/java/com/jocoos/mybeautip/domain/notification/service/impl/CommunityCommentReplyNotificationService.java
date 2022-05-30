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
import com.jocoos.mybeautip.member.comment.CommentRepository;
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

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class CommunityCommentReplyNotificationService implements NotificationService<Comment> {

//    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final MemberNotificationService memberNotificationService;
    private final NotificationCenterRepository notificationCenterRepository;
    private final NotificationMessageCenterRepository messageCenterRepository;
    private final NotificationMessagePushRepository messagePushRepository;

    private final NotificationMessagePushConverter pushConverter;

    private final AppPushService pushService;

    private final TemplateType templateType = TemplateType.COMMUNITY_COMMENT_REPLY;

    @AfterReturning(returning = "result", value = "execution(* com.jocoos.mybeautip.member.comment.CommentService.addComment(..))")
    public void occurs(Object result) {
        if (result instanceof Comment) {
            Comment comment = (Comment) result;
            if (verify(comment)) {
                send(comment);
            }
        } else {
            log.error("Must be Comment(Reply version). But this object is > " + result);
        }
    }

    private boolean verify(Comment comment) {
        return comment.getPostId() != null &&
                comment.getPostId() > 0 &&
                comment.getParentId() != null &&
                comment.getParentId() > 0;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void send(Comment comment) {
        int messageIndex = getMessageRandomIndex();
        Comment parentComment = commentRepository.findById(comment.getParentId())
                .orElseThrow(() -> new BadRequestException("No such Post."));
        NotificationTargetInfo targetInfo = getTargetInfo(parentComment.getCreatedBy().getId());

        Map<String, String> arguments = getArgument(targetInfo.getNickname(), comment);
        NotificationCenterEntity notificationCenterEntity = sendCenter(messageIndex, targetInfo, arguments);
        sendAppPush(messageIndex, notificationCenterEntity.getId(), targetInfo, arguments);
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

    private AppPushMessage getAppPushMessage(int index, long notificationId, Map<String, String> arguments) {
        List<NotificationMessagePushEntity> entities = messagePushRepository.findByTemplateIdAndLastVersionIsTrue(templateType);
        NotificationMessagePushEntity entity = entities.get(index);
        AppPushMessage message = pushConverter.convert(entity, notificationId);
        return message.setArguments(arguments);
    }

    private NotificationTargetInfo getTargetInfo(long memberId) {
        return memberNotificationService.getMemberNotificationInfo(memberId);
    }

    private Map<String, String> getArgument(String nickname, Comment comment) {
        Map<String, String> arguments = new HashMap<>();
        arguments.put(NotificationArgument.USER_NICKNAME.name(), nickname);
        arguments.put(NotificationArgument.POST_ID.name(), String.valueOf(comment.getPostId()));
        arguments.put(NotificationArgument.COMMENT_ID.name(), String.valueOf(comment.getParentId()));
        arguments.put(NotificationArgument.REPLY_ID.name(), String.valueOf(comment.getId()));

        return arguments;
    }
}
