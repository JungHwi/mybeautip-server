package com.jocoos.mybeautip.domain.community.service.dao;

import com.jocoos.mybeautip.domain.community.converter.CommunityConverter;
import com.jocoos.mybeautip.domain.community.dto.WriteCommunityRequest;
import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCategory;
import com.jocoos.mybeautip.domain.community.persistence.repository.CommunityRepository;
import com.jocoos.mybeautip.domain.member.dao.MemberDao;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityDao {

    private final MemberDao memberDao;
    private final CommunityCategoryDao categoryDao;
    private final CommunityRepository repository;

    private final CommunityConverter converter;

    @Transactional
    public Community write(WriteCommunityRequest request) {
        Community community = converter.convert(request);
        CommunityCategory category = categoryDao.getCommunityCategory(request.getCategoryId());
        community.setCategory(category);
        community.setMember(request.getMember());

        community.valid();

        return repository.save(community);
    }

    @Transactional(readOnly = true)
    public Community get(long communityId) {
        return repository.findById(communityId)
                .orElseThrow(() -> new NotFoundException("no_such_community", "No such community. id - " + communityId));
    }

    @Transactional(readOnly = true)
    public List<Community> get(List<CommunityCategory> categoryList, ZonedDateTime cursor, Pageable pageable) {
        return repository.findByCategoryInAndSortedAtLessThan(categoryList, cursor, pageable);
    }

    @Transactional(readOnly = true)
    public List<Community> getCommunityForEvent(long eventId, List<CommunityCategory> categoryList, ZonedDateTime cursor, Pageable pageable) {
        return repository.findByEventIdAndCategoryInAndSortedAtLessThan(eventId, categoryList, cursor, pageable);
    }
}
