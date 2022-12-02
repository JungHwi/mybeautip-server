package com.jocoos.mybeautip.domain.video.service.dao;

import com.jocoos.mybeautip.member.comment.Comment;
import com.jocoos.mybeautip.member.comment.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jocoos.mybeautip.member.comment.Comment.CommentState.DELETED;
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
    public void delete(List<Long> ids) {
        repository.updateState(ids, DELETED.value());
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
}
