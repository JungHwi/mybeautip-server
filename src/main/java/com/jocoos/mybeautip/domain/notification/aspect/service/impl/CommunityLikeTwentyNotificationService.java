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

import static com.jocoos.mybeautip.domain.notification.code.TemplateType.COMMUNITY_LIKE_20;
import static com.jocoos.mybeautip.global.constant.SignConstant.TWENTY;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class CommunityLikeTwentyNotificationService implements AspectNotificationService<CommunityLike> {

    private final CommunityNotificationSendHelper communityNotificationSendHelper;
    private final CommunityLikeDao communityLikeDao;

    @Override
    public TemplateType getTemplateType() {
        return COMMUNITY_LIKE_20;
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
    public void send(CommunityLike communityLike) {
        communityNotificationSendHelper.sendNotification(communityLike.getCommunityId(), getTemplateType());
    }

    private boolean verify(CommunityLike communityLike) {
        int countLike = communityLikeDao.countByCommunityId(communityLike.getCommunityId());
        return countLike == TWENTY && communityLike.getCreatedAt().equals(communityLike.getModifiedAt());
    }
}
