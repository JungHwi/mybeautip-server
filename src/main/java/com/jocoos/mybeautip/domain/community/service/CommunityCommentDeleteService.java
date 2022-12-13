package com.jocoos.mybeautip.domain.community.service;

import com.jocoos.mybeautip.domain.community.code.CommunityStatus;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityComment;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityCommentDao;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityDao;
import com.jocoos.mybeautip.domain.community.vo.CommunityComments;
import com.jocoos.mybeautip.domain.member.service.dao.MemberActivityCountDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static com.jocoos.mybeautip.domain.community.code.CommunityStatus.*;
import static java.lang.Math.toIntExact;

@RequiredArgsConstructor
@Service
public class CommunityCommentDeleteService {

    private final CommunityDao communityDao;
    private final CommunityCommentDao communityCommentDao;
    private final MemberActivityCountDao activityCountDao;

    @Transactional
    public void delete(CommunityComment comment) {
        CommunityComments comments = getComments(comment, NORMAL);
        delete(comments.ids());
        updateCommunityCommentCount(comment.getCommunityId(), -comments.count());
        updateParentCommentCount(comment, -1);
        decreaseActivityCount(comments.activityCountMap());
    }

    @Transactional
    public void delete(Long communityId) {
        CommunityComments comments = getComments(communityId, NORMAL);
        delete(comments.ids());
        updateCommunityCommentCountZero(communityId);
        updateParentCommentCountZero(comments.parentIds());
        decreaseActivityCount(comments.activityCountMap());
    }

    @Transactional
    public void hide(CommunityComment comment, boolean isHide) {
        if (isHide) {
            hide(comment);
        } else {
            show(comment);
        }
    }

    @Transactional
    public void hide(Long communityId, boolean isHide) {
        if (isHide) {
            hide(communityId);
        } else {
            show(communityId);
        }
    }

    private CommunityComments getComments(CommunityComment comment, CommunityStatus status) {
        if (comment.isParent()) {
            List<CommunityComment> children = communityCommentDao.getAllByParentId(comment.getId());
            return new CommunityComments(comment, children, status);
        }
        return new CommunityComments(comment);
    }

    private CommunityComments getComments(Long communityId, CommunityStatus status) {
        List<CommunityComment> comments = communityCommentDao.getAllByCommunityId(communityId);
        return new CommunityComments(comments, status);
    }

    private void hide(CommunityComment comment) {
        CommunityComments comments = getComments(comment, NORMAL);
        hide(comments.ids());
        updateCommunityCommentCount(comment.getCommunityId(), -comments.count());
        updateParentCommentCount(comment, -1);
        decreaseActivityCount(comments.activityCountMap());
    }

    private void show(CommunityComment comment) {
        CommunityComments comments = getComments(comment, HIDE);
        show(comments.ids());
        updateCommunityCommentCount(comment.getCommunityId(), comments.count());
        updateParentCommentCount(comment, 1);
        increaseActivityCount(comments.activityCountMap());
    }

    private void hide(Long communityId) {
        CommunityComments comments = getComments(communityId, NORMAL);
        hide(comments.ids());
        updateCommunityCommentCountZero(communityId);
        updateParentCommentCountZero(comments.parentIds());
        decreaseActivityCount(comments.activityCountMap());
    }

    private void show(Long communityId) {
        CommunityComments comments = getComments(communityId, HIDE);
        show(comments.ids());
        updateCommunityCommentCount(communityId, comments.count());
        updateParentCommentCount(comments.parentCountMap());
        increaseActivityCount(comments.activityCountMap());
    }

    private void updateCommunityCommentCount(Long communityId, int count) {
        communityDao.commentCount(communityId, count);
    }

    private void updateParentCommentCount(CommunityComment comment, int count) {
        if (comment.isParent()) {
            communityCommentDao.setCommentCount(comment.getId(), 0);
        } else {
            communityCommentDao.commentCount(comment.getParentId(), count);
        }
    }

    private void updateParentCommentCount(Map<Long, List<Long>> countCommentIdsMap) {
        for (Map.Entry<Long, List<Long>> entry : countCommentIdsMap.entrySet()) {
            communityCommentDao.setCommentCount(entry.getValue(), toIntExact(entry.getKey()));
        }
    }

    private void updateCommunityCommentCountZero(Long communityId) {
        communityDao.setCommentCount(communityId, 0);
    }

    private void updateParentCommentCountZero(List<Long> parentIds) {
        communityCommentDao.setCommentCount(parentIds, 0);
    }

    private void increaseActivityCount(Map<Long, List<Long>> countMemberIdsMap) {
        for (Map.Entry<Long, List<Long>> entry : countMemberIdsMap.entrySet()) {
            activityCountDao.updateNormalCommunityCommentCount(entry.getValue(), toIntExact(entry.getKey()));
        }
    }

    private void decreaseActivityCount(Map<Long, List<Long>> countMemberIdsMap) {
        for (Map.Entry<Long, List<Long>> entry : countMemberIdsMap.entrySet()) {
            activityCountDao.updateNormalCommunityCommentCount(entry.getValue(), (int) -entry.getKey());
        }
    }

    private void delete(List<Long> ids) {
        communityCommentDao.updateStatus(ids, DELETE);
    }

    private void hide(List<Long> ids) {
        communityCommentDao.updateStatus(ids, HIDE);
    }

    private void show(List<Long> ids) {
        communityCommentDao.updateStatus(ids, NORMAL);
    }
}
