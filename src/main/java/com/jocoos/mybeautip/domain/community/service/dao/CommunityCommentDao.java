package com.jocoos.mybeautip.domain.community.service.dao;

import com.jocoos.mybeautip.domain.community.converter.CommunityCommentConverter;
import com.jocoos.mybeautip.domain.community.dto.SearchCommentRequest;
import com.jocoos.mybeautip.domain.community.dto.WriteCommunityCommentRequest;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityComment;
import com.jocoos.mybeautip.domain.community.persistence.repository.CommunityCommentRepository;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityCommentDao {

    private final CommunityDao communityDao;
    private final CommunityCommentRepository repository;
    private final CommunityCommentConverter converter;

    @Transactional(readOnly = true)
    public List<CommunityComment> getComments(SearchCommentRequest request) {

        // FIXME Dynamic Query to QueryDSL
        Sort.Direction direction = request.getPageable().getSort().stream()
                .map(Sort.Order::getDirection)
                .findAny()
                .orElse(Sort.Direction.DESC);

        Slice<CommunityComment> result = null;

        switch (direction) {
            case ASC:
                result = repository.findByCommunityIdAndParentIdAndIdGreaterThan(request.getCommunityId(), request.getParentId(), request.getCursor(), request.getPageable());
                break;
            case DESC:
                result = repository.findByCommunityIdAndParentIdAndIdLessThan(request.getCommunityId(), request.getParentId(), request.getCursor(), request.getPageable());
        }

        return result.getContent();
    }

    @Transactional(readOnly = true)
    public CommunityComment get(long commentId) {
        return repository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("no_such_comment", "No such comment. id - " + commentId));
    }

    @Transactional(readOnly = true)
    public CommunityComment get(long communityId, long commentId) {
        return repository.findByCommunityIdAndId(communityId, commentId)
                .orElseThrow(() -> new NotFoundException("no_such_comment", "No such comment. communityId - " + communityId + ", commentId - " + commentId));
    }

    @Transactional
    public CommunityComment write(WriteCommunityCommentRequest request) {
        CommunityComment comment = converter.convert(request);
        communityDao.commentCount(request.getCommunityId(), NumberUtils.INTEGER_ONE);

        if (request.getParentId() != null && request.getParentId() > NumberUtils.LONG_ZERO) {
            commentCount(request.getParentId(), NumberUtils.INTEGER_ONE);
        }
        return repository.save(comment);
    }

    @Transactional()
    public void likeCount(long commentId, int count) {
        repository.likeCount(commentId, count);
    }

    @Transactional()
    public void reportCount(long commentId, int count) {
        repository.reportCount(commentId, count);
    }

    @Transactional()
    public void commentCount(long commentId, int count) {
        repository.commentCount(commentId, count);
    }
}
