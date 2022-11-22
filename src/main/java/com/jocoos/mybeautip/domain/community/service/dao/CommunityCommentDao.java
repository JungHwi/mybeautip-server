package com.jocoos.mybeautip.domain.community.service.dao;

import com.jocoos.mybeautip.domain.community.code.CommunityStatus;
import com.jocoos.mybeautip.domain.community.converter.CommunityCommentConverter;
import com.jocoos.mybeautip.domain.community.dto.SearchCommentRequest;
import com.jocoos.mybeautip.domain.community.dto.WriteCommunityCommentRequest;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityComment;
import com.jocoos.mybeautip.domain.community.persistence.repository.CommunityCommentRepository;
import com.jocoos.mybeautip.domain.community.vo.CommunityCommentSearchCondition;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.Collections.singletonList;

@Service
@RequiredArgsConstructor
public class CommunityCommentDao {

    private final CommunityDao communityDao;
    private final CommunityCommentRepository repository;
    private final CommunityCommentConverter converter;

    @Transactional(readOnly = true)
    public List<CommunityComment> getComments(SearchCommentRequest request) {
        CommunityCommentSearchCondition condition =
                new CommunityCommentSearchCondition(request.getCommunityId(), request.getParentId(), request.getMemberId(), request.getCursor());
        return repository.getComments(condition, request.getPageable());
    }

    @Transactional(readOnly = true)
    public Page<CommunityComment> getCommentsPage(Long communityId, Pageable pageable) {
        return repository.findAllByParentIdIsNullAndCommunityId(communityId, pageable);
    }

    @Transactional(readOnly = true)
    public List<CommunityComment> getAllByParentIdIn(List<Long> ids) {
        return repository.findByParentIdInOrderByCreatedAtAsc(ids);
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
                .orElseThrow(() -> new NotFoundException("No such comment. id - " + commentId));
    }

    @Transactional(readOnly = true)
    public CommunityComment get(long communityId, long commentId) {
        return repository.findByCommunityIdAndId(communityId, commentId)
                .orElseThrow(() -> new NotFoundException("No such comment. communityId - " + communityId + ", commentId - " + commentId));
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

    @Transactional(readOnly = true)
    public Long countByMemberId(Long memberId) {
        return repository.countByMemberId(memberId);
    }

    @Transactional(readOnly = true)
    public List<CommunityComment> getAllByParentId(Long parentId) {
        return repository.findByParentId(parentId);
    }

    @Transactional(readOnly = true)
    public List<CommunityComment> getAllByCommunityId(Long communityId) {
        return repository.findByCommunityId(communityId);
    }

    @Transactional
    public void updateStatus(List<Long> ids, CommunityStatus status) {
        repository.updateStatusIdIn(ids, status);
    }

    @Transactional
    public void setCommentCount(List<Long> ids, int count) {
        repository.setCommentCount(ids, count);
    }

    @Transactional
    public void setCommentCount(Long commentId, int count) {
        repository.setCommentCount(singletonList(commentId), count);
    }

}
