package com.jocoos.mybeautip.domain.community.dao;

import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCategory;
import com.jocoos.mybeautip.domain.community.persistence.repository.CommunityCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommunityCategoryDao {

    private final CommunityCategoryRepository repository;

    @Transactional(readOnly = true)
    public CommunityCategory getCommunityCategory(Long communityCategoryId) {
        return repository.getById(communityCategoryId);
    }
}
