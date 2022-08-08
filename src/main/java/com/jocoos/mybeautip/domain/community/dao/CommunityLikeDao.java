package com.jocoos.mybeautip.domain.community.dao;

import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityLike;
import com.jocoos.mybeautip.domain.community.persistence.repository.CommunityLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CommunityLikeDao {

    private final CommunityLikeRepository repository;

    @Transactional(readOnly = true)
    public boolean isLike(long memberId, long communityId) {
        return repository.existsByMemberIdAndCommunityIdAndIsLikeIsTrue(memberId, communityId);
    }

    @Transactional(readOnly = true)
    public List<CommunityLike> isLike(long memberId, List<Long> communityIdList) {
        return repository.findByMemberIdAndCommunityIdInAndIsLikeIsTrue(memberId, communityIdList);
    }
}
