package com.jocoos.mybeautip.domain.community.service.dao;

import com.jocoos.mybeautip.domain.community.code.CommunityStatus;
import com.jocoos.mybeautip.domain.community.converter.CommunityCommentConverter;
import com.jocoos.mybeautip.domain.community.dto.SearchCommentRequest;
import com.jocoos.mybeautip.domain.community.dto.WriteCommunityCommentRequest;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityComment;
import com.jocoos.mybeautip.domain.community.persistence.repository.CommunityCommentRepository;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import io.jsonwebtoken.lang.Collections;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityCommentDao {

    private final CommunityDao communityDao;
    private final CommunityCommentRepository repository;
    private final CommunityCommentConverter converter;

    @Transactional(readOnly = true)
    public List<CommunityComment> getComments(SearchCommentRequest request, List<Long> ids) {

        // FIXME Dynamic Query to QueryDSL
        Sort.Direction direction = request.getPageable().getSort().stream()
                .map(Sort.Order::getDirection)
                .findAny()
                .orElse(Sort.Direction.DESC);

        Slice<CommunityComment> result = null;

        if (Collections.isEmpty(ids)) {
            ids = Arrays.asList(-1L);
        }

        switch (direction) {
            case ASC:
                if (request.getParentId() == null) {
                    result = repository.getAllByAscParentIdNull(request.getCommunityId(), request.getCursor(), ids, request.getPageable());
                } else {
                    result = repository.getAllByAscParentIdNotNull(request.getCommunityId(), request.getParentId(), request.getCursor(), ids, request.getPageable());
                }
                break;
            case DESC:
                if (request.getParentId() == null) {
                    result = repository.getAllByDescParentIdNull(request.getCommunityId(), request.getCursor(), ids, request.getPageable());
                } else {
                    result = repository.getAllByDescParentIdNotNull(request.getCommunityId(), request.getParentId(), request.getCursor(), ids, request.getPageable());
                }
        }

        return result.getContent();
    }

    @Transactional
    public CommunityComment save(CommunityComment communityComment) {
        return repository.save(communityComment);

    }

    @Transactional(readOnly = true)
    public List<CommunityComment> getMyComments(long memberId, long cursor, Pageable pageable) {
        Slice<CommunityComment> commentSlice = repository.findByMemberIdAndStatusAndIdLessThan(memberId, CommunityStatus.NORMAL, cursor, pageable);
        return commentSlice.getContent();
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
        comment.valid();

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
