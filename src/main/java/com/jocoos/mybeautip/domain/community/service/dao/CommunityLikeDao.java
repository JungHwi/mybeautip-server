package com.jocoos.mybeautip.domain.community.service.dao;

import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityLike;
import com.jocoos.mybeautip.domain.community.persistence.repository.CommunityLikeRepository;
import com.jocoos.mybeautip.domain.notification.aspect.annotation.SendNotification;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jocoos.mybeautip.domain.notification.code.TemplateType.COMMUNITY_LIKE_1;
import static com.jocoos.mybeautip.domain.notification.code.TemplateType.COMMUNITY_LIKE_20;


@Service
@RequiredArgsConstructor
public class CommunityLikeDao {

    private final CommunityDao communityDao;
    private final CommunityLikeRepository repository;

    @SendNotification(templateTypes = {COMMUNITY_LIKE_20, COMMUNITY_LIKE_1})
    @Transactional
    public CommunityLike like(long memberId, long communityId, boolean isLike) {
        CommunityLike communityLike = getLike(memberId, communityId);

        if (communityLike.isLike() == isLike) {
            return communityLike;
        }

        communityLike.setLike(isLike);

        communityDao.likeCount(communityId, isLike ? NumberUtils.INTEGER_ONE : NumberUtils.INTEGER_MINUS_ONE);

        return repository.save(communityLike);
    }

    @Transactional(readOnly = true)
    public boolean isLike(long memberId, long communityId) {
        return repository.existsByMemberIdAndCommunityIdAndIsLikeIsTrue(memberId, communityId);
    }

    @Transactional(readOnly = true)
    public List<CommunityLike> likeCommunities(long memberId, List<Long> communityIdList) {
        return repository.findByMemberIdAndCommunityIdInAndIsLikeIsTrue(memberId, communityIdList);
    }

    @Transactional(readOnly = true)
    public CommunityLike getLike(long memberId, long communityId) {
        return repository.findByMemberIdAndCommunityId(memberId, communityId)
                .orElse(new CommunityLike(memberId, communityId));
    }

    @Transactional(readOnly = true)
    public int countByCommunityId(long communityId) {
        return repository.countByCommunityId(communityId);

    }
}
