package com.jocoos.mybeautip.domain.community.persistence.repository.comment;

import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityComment;
import com.jocoos.mybeautip.domain.community.vo.CommunityCommentSearchCondition;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommunityCommentCustomRepository {
    List<CommunityComment> getComments(CommunityCommentSearchCondition condition, Pageable pageable);
}
