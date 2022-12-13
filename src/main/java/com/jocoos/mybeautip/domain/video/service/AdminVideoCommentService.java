package com.jocoos.mybeautip.domain.video.service;

import com.jocoos.mybeautip.domain.video.converter.VideoCommentConverter;
import com.jocoos.mybeautip.domain.video.dto.AdminVideoCommentResponse;
import com.jocoos.mybeautip.domain.video.service.dao.VideoCommentDao;
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
    private final VideoCommentDeleteService deleteService;
    private final VideoCommentConverter converter;

    @Transactional
    public AdminVideoCommentResponse write(Long videoId, String content, Long parentId) {
        Comment comment = converter.convert(videoId, content, parentId);
        Comment savedComment = videoCommentDao.save(comment);
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
}
