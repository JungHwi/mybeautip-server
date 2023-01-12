package com.jocoos.mybeautip.domain.video.service;

import com.jocoos.mybeautip.client.aws.s3.AwsS3Handler;
import com.jocoos.mybeautip.domain.video.converter.VideoCommentConverter;
import com.jocoos.mybeautip.domain.video.dto.AdminVideoCommentResponse;
import com.jocoos.mybeautip.domain.video.dto.PatchVideoCommentRequest;
import com.jocoos.mybeautip.domain.video.dto.WriteVideoCommentRequest;
import com.jocoos.mybeautip.domain.video.service.dao.VideoCommentDao;
import com.jocoos.mybeautip.domain.video.service.dao.VideoDao;
import com.jocoos.mybeautip.global.util.JsonNullableUtils;
import com.jocoos.mybeautip.global.wrapper.PageResponse;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.comment.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.jocoos.mybeautip.global.code.UrlDirectory.VIDEO_COMMENT;

@RequiredArgsConstructor
@Service
public class AdminVideoCommentService {

    private final VideoCommentDao videoCommentDao;
    private final VideoDao videoDao;
    private final VideoCommentDeleteService deleteService;
    private final VideoCommentConverter converter;
    private final AwsS3Handler awsS3Handler;

    @Transactional
    public AdminVideoCommentResponse write(Long videoId, WriteVideoCommentRequest request) {
        Comment comment = converter.convert(videoId, request);
        Comment savedComment = videoCommentDao.save(comment);
        updateCommentCount(videoId, savedComment);
        if (request.file() != null) {
            awsS3Handler.copy(request.file(), VIDEO_COMMENT.getDirectory(comment.getId()));
        }
        return new AdminVideoCommentResponse(savedComment);
    }

    @Transactional
    public Long edit(Long videoId, Long commentId, Member editor, PatchVideoCommentRequest request) {
        Comment comment = videoCommentDao.get(videoId, commentId);
        String editContents = JsonNullableUtils.getIfPresent(request.getContents(), comment.getComment());
        comment.edit(editContents, request.fileDtoToFiles(), editor);

        awsS3Handler.editFiles(request.getFiles(), VIDEO_COMMENT.getDirectory(comment.getId()));
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
