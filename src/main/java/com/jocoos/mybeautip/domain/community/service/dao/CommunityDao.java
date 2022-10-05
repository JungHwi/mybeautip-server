package com.jocoos.mybeautip.domain.community.service.dao;

import com.jocoos.mybeautip.domain.community.code.CommunityStatus;
import com.jocoos.mybeautip.domain.community.converter.CommunityConverter;
import com.jocoos.mybeautip.domain.community.dto.WriteCommunityRequest;
import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCategory;
import com.jocoos.mybeautip.domain.community.persistence.repository.CommunityRepository;
import com.jocoos.mybeautip.domain.community.vo.CommunitySearchCondition;
import com.jocoos.mybeautip.domain.search.vo.KeywordSearchCondition;
import com.jocoos.mybeautip.domain.search.vo.SearchResult;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityDao {

    private final CommunityCategoryDao categoryDao;
    private final CommunityRepository repository;
    private final CommunityConverter converter;

    private final EntityManager em;

    @Transactional
    public Community write(WriteCommunityRequest request) {
        CommunityCategory category = categoryDao.getCommunityCategory(request.getCategoryId());
        request.setCategory(category);

        Community community = converter.convert(request);
        community.setMember(request.getMember());

        community.valid();

        return repository.save(community);
    }

    @Transactional
    public Community save(Community community) {
        return repository.save(community);
    }

    @Transactional(readOnly = true)
    public Community get(long communityId) {
        return repository.findById(communityId)
                .orElseThrow(() -> new NotFoundException("No such community. id - " + communityId));
    }

    @Transactional(readOnly = true)
    public Community getUpdated(Long communityId) {
        em.flush();
        em.clear();
        return get(communityId);
    }

    @Transactional(readOnly = true)
    public List<Community> get(long memberId, long id, Pageable pageable) {
        return repository.findByMemberIdAndStatusAndIdLessThan(memberId, CommunityStatus.NORMAL, id, pageable).getContent();
    }

    @Transactional(readOnly = true)
    public List<Community> get(List<CommunityCategory> categoryList, ZonedDateTime cursor, Pageable pageable) {
        return repository.findByCategoryInAndSortedAtLessThan(categoryList, cursor, pageable).getContent();
    }

    @Transactional
    public List<Community> getCommunities(CommunitySearchCondition condition, Pageable pageable) {
        return repository.getCommunities(condition, pageable);
    }

    @Transactional()
    public void readCount(long communityId) {
        repository.readCount(communityId);
    }

    @Transactional()
    public void likeCount(long communityId, int count) {
        repository.likeCount(communityId, count);
    }

    @Transactional()
    public void reportCount(long communityId, int count) {
        repository.reportCount(communityId, count);
    }

    @Transactional()
    public void commentCount(long communityId, int count) {
        repository.commentCount(communityId, count);
    }

    @Transactional
    public void updateSortedAt(long communityId) {
        repository.updateSortedAt(communityId, ZonedDateTime.now());
    }

    @Transactional(readOnly = true)
    public SearchResult<Community> search(KeywordSearchCondition condition) {
        return repository.search(condition);
    }

    @Transactional(readOnly = true)
    public Long count(String keyword) {
        return repository.countBy(keyword);
    }
}
