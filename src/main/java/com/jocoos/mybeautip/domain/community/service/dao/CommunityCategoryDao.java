package com.jocoos.mybeautip.domain.community.service.dao;

import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCategory;
import com.jocoos.mybeautip.domain.community.persistence.repository.CommunityCategoryRepository;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityCategoryDao {

    private final CommunityCategoryRepository repository;

    @Transactional(readOnly = true)
    public CommunityCategory getCommunityCategory(Long communityCategoryId) {
        return repository.findById(communityCategoryId)
                .orElseThrow(() -> new BadRequestException("no_such_category", "No such category. id - " + communityCategoryId));
    }

    @Transactional(readOnly = true)
    public List<CommunityCategory> getCommunityCategory(List<Long> categoryIds) {
        return repository.findAllByIdIn(categoryIds);
    }

    @Transactional(readOnly = true)
    public List<CommunityCategory> getCategoryForSearchCommunity(Long communityCategoryId) {
        CommunityCategory category = getCommunityCategory(communityCategoryId);

        if (category.getType() == CommunityCategoryType.TOTAL) {
            return repository.findAllByParentId(category.getId());
        } else {
            return Collections.singletonList(category);
        }
    }
}