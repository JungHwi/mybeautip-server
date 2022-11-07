package com.jocoos.mybeautip.member.comment;

import com.jocoos.mybeautip.restapi.CommentSearchCondition;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentCustomRepository {
    List<Comment> getComments(CommentSearchCondition condition, Pageable pageable);
}
