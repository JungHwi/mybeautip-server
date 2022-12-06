package com.jocoos.mybeautip.domain.video.service;

import com.jocoos.mybeautip.domain.member.service.dao.MemberActivityCountDao;
import com.jocoos.mybeautip.domain.video.service.dao.VideoCommentDao;
import com.jocoos.mybeautip.domain.video.service.dao.VideoDao;
import com.jocoos.mybeautip.domain.video.vo.VideoComments;
import com.jocoos.mybeautip.member.comment.Comment;
import com.jocoos.mybeautip.member.comment.Comment.CommentState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static com.jocoos.mybeautip.member.comment.Comment.CommentState.*;
import static java.lang.Math.toIntExact;

@RequiredArgsConstructor
@Service
@Slf4j
public class VideoCommentDeleteService {

    private final VideoCommentDao videoCommentDao;
    private final VideoDao videoDao;
    private final MemberActivityCountDao activityCountDao;

    @Transactional
    public int delete(Comment comment) {
        VideoComments comments = getComments(comment, DEFAULT);
        delete(comments.ids());
        updateVideoCommentCount(comment.getVideoId(), -comments.count());
        updateParentCommentCount(comment, -1);
        decreaseActivityCount(comments.activityCountMap());

        log.info("deleted comment count for video: {}", comments.count());

        return DELETED.value();
    }

    @Transactional
    public void delete(Long videoId) {
        VideoComments comments = getComments(videoId, DEFAULT);
        delete(comments.ids());
        updateVideoCommentCountZero(videoId);
        updateParentCommentCountZero(comments.parentIds());
        decreaseActivityCount(comments.activityCountMap());
    }

    @Transactional
    public void hide(Comment comment, boolean isHide) {
        if (isHide) {
            hide(comment);
        } else {
            show(comment);
        }
    }

    @Transactional
    public void hide(Long videoId, boolean isHide) {
        if (isHide) {
            hide(videoId);
        } else {
            show(videoId);
        }
    }

    private VideoComments getComments(Comment comment, CommentState state) {
        if (comment.isParent()) {
            List<Comment> children = videoCommentDao.getAllByParentId(comment.getId());
            return new VideoComments(comment, children, state);
        }
        return new VideoComments(comment);
    }

    private VideoComments getComments(Long videoId, CommentState state) {
        List<Comment> comments = videoCommentDao.getAllByVideoId(videoId);
        return new VideoComments(comments, state);
    }

    private void hide(Comment comment) {
        VideoComments comments = getComments(comment, DEFAULT);
        hide(comments.ids());
        updateVideoCommentCount(comment.getVideoId(), -comments.count());
        updateParentCommentCount(comment, -1);
        decreaseActivityCount(comments.activityCountMap());
    }

    private void show(Comment comment) {
        VideoComments comments = getComments(comment, BLINDED_BY_ADMIN);
        show(comments.ids());
        updateVideoCommentCount(comment.getVideoId(), comments.count());
        updateParentCommentCount(comment, 1);
        increaseActivityCount(comments.activityCountMap());
    }

    private void hide(Long videoId) {
        VideoComments comments = getComments(videoId, DEFAULT);
        hide(comments.ids());
        updateVideoCommentCountZero(videoId);
        updateParentCommentCountZero(comments.parentIds());
        decreaseActivityCount(comments.activityCountMap());
    }

    private void show(Long videoId) {
        VideoComments comments = getComments(videoId, BLINDED_BY_ADMIN);
        show(comments.ids());
        updateVideoCommentCount(videoId, comments.count());
        updateParentCommentCount(comments.parentCountMap());
        increaseActivityCount(comments.activityCountMap());
    }

    private void updateVideoCommentCount(Long videoId, int count) {
        videoDao.commentCount(videoId, count);
    }

    private void updateParentCommentCount(Comment comment, int count) {
        if (comment.isParent()) {
            videoCommentDao.setCommentCount(comment.getId(), 0);
        } else {
            videoCommentDao.commentCount(comment.getParentId(), count);
        }
    }

    private void updateParentCommentCountZero(List<Long> parentIds) {
        videoCommentDao.setCommentCount(parentIds, 0);
    }

    private void updateVideoCommentCountZero(Long videoId) {
        videoDao.setCommentCount(videoId, 0);
    }

    private void updateParentCommentCount(Map<Long, List<Long>> countCommentIdsMap) {
        for (Map.Entry<Long, List<Long>> entry : countCommentIdsMap.entrySet()) {
            videoCommentDao.setCommentCount(entry.getValue(), toIntExact(entry.getKey()));
        }
    }

    private void increaseActivityCount(Map<Long, List<Long>> countMemberIdsMap) {
        for (Map.Entry<Long, List<Long>> entry : countMemberIdsMap.entrySet()) {
            activityCountDao.updateNormalCommunityCommentCount(entry.getValue(), toIntExact(entry.getKey()));
        }
    }

    private void decreaseActivityCount(Map<Long, List<Long>> countMemberIdsMap) {
        for (Map.Entry<Long, List<Long>> entry : countMemberIdsMap.entrySet()) {
            activityCountDao.updateNormalVideoCommentCount(entry.getValue(), (int) -entry.getKey());
        }
    }

    private void hide(List<Long> ids) {
        videoCommentDao.updateStatus(ids, BLINDED_BY_ADMIN);
    }

    private void delete(List<Long> ids) {
        videoCommentDao.updateStatus(ids, DELETED);
    }

    private void show(List<Long> ids) {
        videoCommentDao.updateStatus(ids, DEFAULT);
    }

}
