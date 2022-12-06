package com.jocoos.mybeautip.domain.community.service.dao;

import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType;
import com.jocoos.mybeautip.domain.community.code.CommunityStatus;
import com.jocoos.mybeautip.domain.community.converter.CommunityConverter;
import com.jocoos.mybeautip.domain.community.dto.AdminCommunityResponse;
import com.jocoos.mybeautip.domain.community.dto.WriteCommunityRequest;
import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCategory;
import com.jocoos.mybeautip.domain.community.persistence.repository.CommunityRepository;
import com.jocoos.mybeautip.domain.community.vo.CommunitySearchCondition;
import com.jocoos.mybeautip.domain.home.vo.SummaryCommunityCondition;
import com.jocoos.mybeautip.domain.home.vo.SummaryCommunityResult;
import com.jocoos.mybeautip.domain.search.vo.KeywordSearchCondition;
import com.jocoos.mybeautip.domain.search.vo.SearchResult;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

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
        community.validWrite();

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
    public List<Community> getCommunities(CommunitySearchCondition condition, Pageable pageable) {
        return repository.getCommunities(condition, pageable);
    }

    @Transactional(readOnly = true)
    public Page<AdminCommunityResponse> getCommunitiesAllStatus(CommunitySearchCondition condition) {
        return repository.getCommunitiesAllStatus(condition);
    }

    @Transactional(readOnly = true)
    public List<Community> get(List<Long> ids) {
        return repository.findByIdIn(ids);
    }

    @Transactional(readOnly = true)
    public List<SummaryCommunityResult> summary(Long categoryId, CommunityCategoryType type, int size, Long memberId) {
        return repository.summary(new SummaryCommunityCondition(categoryId, type, size, memberId));
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
    public Long countBy(String keyword, Long memberId) {
        return repository.countBy(keyword, memberId);
    }

    @Transactional(readOnly = true)
    public Long countBy(Long memberId) {
        return repository.countByMemberId(memberId);
    }

    @Transactional
    public Long fix(Long communityId) {
        findTopFix().ifPresent(topCommunity -> topCommunity.fix(false));
        Community community = get(communityId);
        community.fix(true);
        return community.getId();
    }

    private Optional<Community> findTopFix() {
        return repository.findByIsTopFixIsTrue();
    }

    @Transactional
    public Long nonFix(Long communityId) {
        Community community = get(communityId);
        community.fix(false);
        return community.getId();
    }

    public void setCommentCount(Long communityId, int count) {
        repository.setCommentCount(communityId, count);
    }

    public void hardDelete(Community community) {
        repository.delete(community);
    }
}
