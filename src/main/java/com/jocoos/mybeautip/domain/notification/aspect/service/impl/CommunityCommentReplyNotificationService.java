package com.jocoos.mybeautip.domain.notification.aspect.service.impl;

import com.jocoos.mybeautip.domain.community.dto.CommunityCommentResponse;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityComment;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityCommentDao;
import com.jocoos.mybeautip.domain.notification.aspect.service.AspectNotificationService;
import com.jocoos.mybeautip.domain.notification.service.NotificationSendService;
import com.jocoos.mybeautip.domain.notification.code.NotificationArgument;
import com.jocoos.mybeautip.domain.notification.code.TemplateType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class CommunityCommentReplyNotificationService implements AspectNotificationService<CommunityCommentResponse> {

    private static final TemplateType TEMPLATE_TYPE = TemplateType.COMMUNITY_COMMENT_REPLY;
    private final CommunityCommentDao commentDao;
    private final NotificationSendService sendService;

    @Override
    public TemplateType getTemplateType() {
        return TEMPLATE_TYPE;
    }

    @Override
    public void occurs(Object result) {
        if (result instanceof CommunityCommentResponse comment) {
            if (verify(comment)) {
                send(comment);
            }
        } else {
            log.error("Must be Comment(Reply version). But this object is > " + result);
        }
    }

    @Override
    public void send(CommunityCommentResponse comment) {
        CommunityComment parentComment = commentDao.get(comment.getParentId());
        Map<String, String> arguments = getArgument(comment);
        sendService.send(TEMPLATE_TYPE, parentComment.getMemberId(), null, arguments);
    }

    private boolean verify(CommunityCommentResponse comment) {
        return comment.getCommunityId() != null &&
                comment.getCommunityId() > 0 &&
                comment.getParentId() != null &&
                comment.getParentId() > 0;
    }

    private Map<String, String> getArgument(CommunityCommentResponse comment) {
        Map<String, String> arguments = new HashMap<>();
        arguments.put(NotificationArgument.COMMUNITY_ID.name(), String.valueOf(comment.getCommunityId()));
        arguments.put(NotificationArgument.COMMENT_ID.name(), String.valueOf(comment.getParentId()));
        arguments.put(NotificationArgument.REPLY_ID.name(), String.valueOf(comment.getId()));
        return arguments;
    }
}
