package com.jocoos.mybeautip.domain.community.service.dao;

import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityLike;
import com.jocoos.mybeautip.domain.community.persistence.repository.CommunityLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CommunityLikeDao {

    private final CommunityDao communityDao;
    private final CommunityLikeRepository repository;

    @Transactional
    public CommunityLike like(long memberId, long communityId, boolean isLike) {
        CommunityLike communityLike = getLike(memberId, communityId);

        if (communityLike.isLike() == isLike) {
            return communityLike;
        }

        communityLike.setLike(isLike);

        communityDao.likeCount(communityId, isLike ? 1 : -1);

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
}
