package com.jocoos.mybeautip.domain.video.service;

import com.jocoos.mybeautip.domain.member.service.dao.MemberActivityCountDao;
import com.jocoos.mybeautip.domain.video.service.dao.VideoCommentDao;
import com.jocoos.mybeautip.domain.video.service.dao.VideoDao;
import com.jocoos.mybeautip.domain.video.vo.VideoComments;
import com.jocoos.mybeautip.member.comment.Comment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static com.jocoos.mybeautip.member.comment.Comment.CommentState.DELETED;

@RequiredArgsConstructor
@Service
@Slf4j
public class VideoCommentDeleteService {

    private final VideoCommentDao videoCommentDao;
    private final VideoDao videoDao;
    private final MemberActivityCountDao activityCountDao;

    @Transactional
    public int delete(Comment comment) {
        VideoComments comments = getComments(comment);
        delete(comments.ids());
        updateVideoCommentCount(comment, comments.count());
        updateParentCommentCount(comment);
        decreaseActivityCount(comments.activityCountMap());

        log.info("deleted comment count for video: {}", comments.count());

        return DELETED.value();
    }

    private VideoComments getComments(Comment comment) {
        if (comment.isParent()) {
            List<Comment> children = videoCommentDao.getAllByParentId(comment.getId());
            return new VideoComments(comment, children);
        }
        return new VideoComments(comment);
    }

    private void delete(List<Long> ids) {
        videoCommentDao.delete(ids);
    }

    private void updateVideoCommentCount(Comment comment, int count) {
        videoDao.commentCount(comment.getVideoId(), -count);
    }

    private void updateParentCommentCount(Comment comment) {
        if (comment.isParent()) {
            videoCommentDao.setCommentCount(comment.getId(), 0);
        } else {
            videoCommentDao.commentCount(comment.getParentId(), -1);
        }
    }

    private void decreaseActivityCount(Map<Long, List<Long>> countMemberIdsMap) {
        for (Map.Entry<Long, List<Long>> entry : countMemberIdsMap.entrySet()) {
            activityCountDao.updateNormalVideoCommentCount(entry.getValue(), (int) -entry.getKey());
        }
    }

    @Transactional
    public void delete(Long videoId) {
        VideoComments comments = getComments(videoId);
        delete(comments.ids());
        updateVideoCommentCountZero(videoId);
        updateParentCommentCountZero(comments.parentIds());
        decreaseActivityCount(comments.activityCountMap());
    }

    private void updateParentCommentCountZero(List<Long> parentIds) {
        videoCommentDao.setCommentCount(parentIds, 0);
    }

    private void updateVideoCommentCountZero(Long videoId) {
        videoDao.setCommentCount(videoId, 0);
    }

    private VideoComments getComments(Long videoId) {
        List<Comment> comments = videoCommentDao.getAllByVideoId(videoId);
        return new VideoComments(comments);
    }
}
