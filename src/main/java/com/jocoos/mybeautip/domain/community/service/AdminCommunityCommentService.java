package com.jocoos.mybeautip.domain.community.service;

import com.jocoos.mybeautip.domain.community.converter.AdminCommunityCommentConverter;
import com.jocoos.mybeautip.domain.community.dto.AdminCommunityCommentResponse;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCategory;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityComment;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityCategoryDao;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityCommentDao;
import com.jocoos.mybeautip.global.wrapper.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AdminCommunityCommentService {

    private final CommunityCommentDao communityCommentDao;
    private final CommunityCategoryDao categoryDao;
    private final AdminCommunityCommentConverter converter;

    @Transactional(readOnly = true)
    public PageResponse<AdminCommunityCommentResponse> getComments(Long communityId, Pageable pageable) {



        Page<CommunityComment> page = communityCommentDao.getCommentsPage(communityId, pageable);

        Long categoryId = page.stream()
                .map(CommunityComment::getCategoryId)
                .findFirst()
                .orElse(null);

        CommunityCategory category = categoryDao.getCommunityCategory(categoryId);


        List<Long> ids = page.stream()
                .map(CommunityComment::getId)
                .toList();

        List<CommunityComment> children = communityCommentDao.getAllByParentIdIn(ids);
        List<AdminCommunityCommentResponse> content = converter.convert(page.getContent(), children, category);



        return new PageResponse<>(page.getTotalElements(), content);
    }

    @Transactional
    public Long hide(Long commentId, boolean isHide) {
        CommunityComment comment = communityCommentDao.get(commentId);
        comment.hide(isHide);
        return comment.getId();
    }
}