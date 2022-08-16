package com.jocoos.mybeautip.domain.community.service.dao;

import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCommentLike;
import com.jocoos.mybeautip.domain.community.persistence.repository.CommunityCommentLikeRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CommunityCommentLikeDao {

    private final CommunityCommentDao communityDao;
    private final CommunityCommentLikeRepository repository;

    @Transactional
    public CommunityCommentLike like(long memberId, long commentId, boolean isLike) {
        CommunityCommentLike communityCommentLike = getCommentLike(memberId, commentId);

        if (communityCommentLike.isLike() == isLike) {
            return communityCommentLike;
        }

        communityCommentLike.setLike(isLike);

        communityDao.likeCount(commentId, isLike ? NumberUtils.INTEGER_ONE : NumberUtils.INTEGER_MINUS_ONE);

        return repository.save(communityCommentLike);
    }

    @Transactional(readOnly = true)
    public boolean isLike(long memberId, long commentId) {
        return repository.existsByMemberIdAndCommentIdAndIsLikeIsTrue(memberId, commentId);
    }

    @Transactional(readOnly = true)
    public List<CommunityCommentLike> likeComments(long memberId, List<Long> commentIds) {
        return repository.findByMemberIdAndCommentIdInAndIsLikeIsTrue(memberId, commentIds);
    }

    @Transactional(readOnly = true)
    public CommunityCommentLike getCommentLike(long memberId, long communityId) {
        return repository.findByMemberIdAndCommentId(memberId, communityId)
                .orElse(new CommunityCommentLike(memberId, communityId));
    }
}
