package com.jocoos.mybeautip.domain.community.service;

import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType;
import com.jocoos.mybeautip.domain.community.dto.WriteCommunityCommentRequest;
import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityComment;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityCommentDao;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityDao;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CommunityCommentCRUDService {

    private final CommunityDao communityDao;
    private final CommunityCommentDao dao;
    private final CommunityFileService fileService;


    @Transactional
    public CommunityComment write(WriteCommunityCommentRequest request) {
        valid(request);

        Community community = communityDao.get(request.getCommunityId());
        request.setCategoryId(community.getCategoryId());

        CommunityComment communityComment = dao.write(request);
        fileService.write(request.getFile(), communityComment.getId());

        if (community.getCategory().getType() == CommunityCategoryType.BLIND) {
            communityDao.updateSortedAt(community.getId());
        }

        return communityComment;
    }


    private void valid(WriteCommunityCommentRequest request) {
        if (request.getParentId() != null && request.getParentId() > NumberUtils.LONG_ZERO) {
            validReply(request);
        }
    }

    private void validReply(WriteCommunityCommentRequest request) {
        CommunityComment parentComment = dao.get(request.getParentId());
        if (!request.getCommunityId().equals(parentComment.getCommunityId())) {
            throw new BadRequestException("Not matched Community Info. Parent comment's community id - " + parentComment.getCommunityId());
        }
    }
}
