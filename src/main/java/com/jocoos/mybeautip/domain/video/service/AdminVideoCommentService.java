package com.jocoos.mybeautip.domain.video.service;

import com.jocoos.mybeautip.domain.video.converter.VideoCommentConverter;
import com.jocoos.mybeautip.domain.video.dto.AdminVideoCommentResponse;
import com.jocoos.mybeautip.domain.video.dto.WriteVideoCommentRequest;
import com.jocoos.mybeautip.domain.video.service.dao.VideoCommentDao;
import com.jocoos.mybeautip.domain.video.service.dao.VideoDao;
import com.jocoos.mybeautip.global.wrapper.PageResponse;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.comment.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AdminVideoCommentService {

    private final VideoCommentDao videoCommentDao;
    private final VideoDao videoDao;
    private final VideoCommentDeleteService deleteService;
    private final VideoCommentConverter converter;

    @Transactional
    public AdminVideoCommentResponse write(Long videoId, WriteVideoCommentRequest request) {
        Comment comment = converter.convert(videoId, request);
        Comment savedComment = videoCommentDao.save(comment);
        updateCommentCount(videoId, savedComment);
        return new AdminVideoCommentResponse(savedComment);
    }

    @Transactional
    public Long edit(Long videoId, Long commentId, Member editor, String editedComment) {
        Comment comment = videoCommentDao.get(videoId, commentId);
        comment.editComment(editedComment, editor);
        return comment.getId();
    }

    @Transactional(readOnly = true)
    public PageResponse<AdminVideoCommentResponse> getVideoComments(Long videoId, Pageable pageable) {
        Page<AdminVideoCommentResponse> page = videoCommentDao.getCommentsWithChild(videoId, pageable);
        return new PageResponse<>(page.getTotalElements(), page.getContent());
    }

    @Transactional
    public Long hide(Long videoId, Long commentId, boolean isHide) {
        Comment comment = videoCommentDao.get(videoId, commentId);
        comment.hide(isHide);
        deleteService.hide(comment, isHide);
        return comment.getId();
    }

    private void updateCommentCount(Long videoId, Comment savedComment) {
        videoDao.commentCount(videoId, 1);
        if (savedComment.isChild()) {
            videoCommentDao.commentCount(savedComment.getParentId(), 1);
        }
    }
}
