package com.jocoos.mybeautip.domain.notification.aspect.service.impl;

import com.jocoos.mybeautip.domain.community.dto.CommunityCommentResponse;
import com.jocoos.mybeautip.domain.notification.aspect.service.AspectNotificationService;
import com.jocoos.mybeautip.domain.notification.aspect.service.CommunityNotificationSendHelper;
import com.jocoos.mybeautip.domain.notification.code.TemplateType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import static com.jocoos.mybeautip.domain.notification.code.TemplateType.COMMUNITY_COMMENT;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class CommunityCommentNotificationService implements AspectNotificationService<CommunityCommentResponse> {

    private final CommunityNotificationSendHelper communityNotificationSendHelper;

    @Override
    public TemplateType getTemplateType() {
        return COMMUNITY_COMMENT;
    }

    @Override
    public void occurs(Object result) {
        if (result instanceof CommunityCommentResponse comment) {
            if (verify(comment)) {
                send(comment);
            }
        } else {
            log.error("Must be Comment. But this object is > " + result);
        }
    }

    @Override
    public void send(CommunityCommentResponse comment) {
        communityNotificationSendHelper.sendNotification(comment.getCommunityId(), getTemplateType());
    }

    private boolean verify(CommunityCommentResponse comment) {
        return comment.getCommunityId() != null &&
                comment.getCommunityId() > 0 &&
                comment.getParentId() == null;
    }
}
