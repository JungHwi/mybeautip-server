package com.jocoos.mybeautip.domain.member.service.activity.impl;

import com.jocoos.mybeautip.domain.community.converter.CommunityCommentConverter;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityComment;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityCommentDao;
import com.jocoos.mybeautip.domain.member.code.MemberActivityType;
import com.jocoos.mybeautip.domain.member.dto.MemberActivityRequest;
import com.jocoos.mybeautip.domain.member.dto.MyCommunityCommentResponse;
import com.jocoos.mybeautip.domain.member.service.activity.MyActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.lang.Long.MAX_VALUE;

@RequiredArgsConstructor
@Service
public class CommunityCommentActivityService implements MyActivityService<MyCommunityCommentResponse> {

    private final CommunityCommentDao dao;
    private final CommunityCommentConverter converter;

    @Override
    public MemberActivityType getType() {
        return MemberActivityType.COMMUNITY_COMMENT;
    }

    @Transactional(readOnly = true)
    @Override
    public List<MyCommunityCommentResponse> getMyActivity(MemberActivityRequest request) {
        long idCursor = request.idCursor() == null ? MAX_VALUE : request.idCursor();
        Pageable pageable = PageRequest.of(0, request.size(), Sort.by(Sort.Direction.DESC, "id"));
        List<CommunityComment> communityComments = dao.getMyComments(request.member().getId(), idCursor, pageable);
        List<MyCommunityCommentResponse> responses = converter.convertToMyComment(communityComments);
        for (MyCommunityCommentResponse response : responses) {
            response.changeContents();
        }
        return responses;
    }
}
