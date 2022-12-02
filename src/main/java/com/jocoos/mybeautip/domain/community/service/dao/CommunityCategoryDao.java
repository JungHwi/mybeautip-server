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
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CommunityCategoryDao {

    private final CommunityCategoryRepository repository;

    @Transactional(readOnly = true)
    public CommunityCategory getCommunityCategory(Long communityCategoryId) {
        return repository.findById(communityCategoryId)
                .orElseThrow(() -> new BadRequestException("No such category. id - " + communityCategoryId));
    }

    @Transactional(readOnly = true)
    public List<CommunityCategory> getCommunityCategory(List<Long> categoryIds) {
        return repository.findAllByIdIn(categoryIds);
    }

    @Transactional(readOnly = true)
    public List<CommunityCategory> getCategoryForSearchCommunity(Long communityCategoryId) {
        CommunityCategory category = getCommunityCategory(communityCategoryId);

        if (category.getType() == CommunityCategoryType.GROUP) {
            return repository.findAllByParentId(category.getParentId());
        } else {
            return Collections.singletonList(category);
        }
    }

    @Transactional(readOnly = true)
    public List<CommunityCategory> topSummaryCategories() {
        return repository.findAllByIsInSummaryIsTrue();
    }

    @Transactional(readOnly = true)
    public CommunityCategory getByType(CommunityCategoryType type) {
        return repository.findByType(type).orElseThrow(
                () -> new BadRequestException("No such category. type - " + type)
        );
    }

    @Transactional(readOnly = true)
    public List<CommunityCategory> getAllExcludeByTypes(Set<CommunityCategoryType> types) {
        return repository.findAllByTypeNotInAndParentIdIsNotNull(types);
    }
}
