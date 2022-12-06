package com.jocoos.mybeautip.domain.video.service.dao;

import com.jocoos.mybeautip.domain.video.dto.AdminVideoCommentResponse;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import com.jocoos.mybeautip.member.comment.Comment;
import com.jocoos.mybeautip.member.comment.Comment.CommentState;
import com.jocoos.mybeautip.member.comment.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.Collections.singletonList;

@RequiredArgsConstructor
@Service
public class VideoCommentDao {

    private final CommentRepository repository;

    @Transactional(readOnly = true)
    public List<Comment> getAllByParentId(Long parentId) {
        return repository.findByParentId(parentId);
    }

    @Transactional
    public void updateStatus(List<Long> ids, CommentState state) {
        repository.updateState(ids, state.value());
    }

    @Transactional
    public void commentCount(Long id, int count) {
        repository.updateCommentCount(id, count);
    }

    @Transactional(readOnly = true)
    public List<Comment> getAllByVideoId(Long videoId) {
        return repository.findByVideoId(videoId);
    }

    @Transactional
    public void setCommentCount(List<Long> parentIds, int count) {
        repository.setCommentCount(parentIds, count);
    }

    @Transactional
    public void setCommentCount(Long commentId, int count) {
        repository.setCommentCount(singletonList(commentId), count);
    }

    @Transactional(readOnly = true)
    public Page<AdminVideoCommentResponse> getCommentsWithChild(Long videoId, Pageable pageable) {
        return repository.getCommentsWithChild(videoId, pageable);
    }

    public Comment get(Long commentId) {
        return repository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("video comment not exists, id - " + commentId));
    }
}

