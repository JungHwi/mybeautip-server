package com.jocoos.mybeautip.domain.notification.aspect.service.impl;

import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityLike;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityLikeDao;
import com.jocoos.mybeautip.domain.notification.aspect.service.AspectNotificationService;
import com.jocoos.mybeautip.domain.notification.aspect.service.CommunityNotificationSendHelper;
import com.jocoos.mybeautip.domain.notification.code.TemplateType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import static com.jocoos.mybeautip.domain.notification.code.TemplateType.COMMUNITY_LIKE_1;
import static com.jocoos.mybeautip.global.constant.SignConstant.ONE;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class CommunityLikeFirstNotificationService implements AspectNotificationService<CommunityLike> {

    private final CommunityNotificationSendHelper communityNotificationSendHelper;
    private final CommunityLikeDao communityLikeDao;

    @Override
    public TemplateType getTemplateType() {
        return COMMUNITY_LIKE_1;
    }

    @Override
    public void occurs(Object result) {
        if (result instanceof CommunityLike communityLike) {
            if (verify(communityLike)) {
                send(communityLike);
            }
        } else {
            log.error("Must be CommunityLike Object. But this object is > " + result);
        }
    }

    @Override
    public void send(CommunityLike postLike) {
        communityNotificationSendHelper.sendNotification(postLike.getCommunityId(), getTemplateType());
    }

    private boolean verify(CommunityLike communityLike) {
        int countLike = communityLikeDao.countByCommunityId(communityLike.getCommunityId());
        return countLike == ONE && communityLike.getCreatedAt().equals(communityLike.getModifiedAt());
    }
}
