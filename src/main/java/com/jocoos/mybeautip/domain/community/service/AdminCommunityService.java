package com.jocoos.mybeautip.domain.community.service;

import com.jocoos.mybeautip.domain.community.converter.AdminCommunityConverter;
import com.jocoos.mybeautip.domain.community.converter.CommunityConverter;
import com.jocoos.mybeautip.domain.community.dto.AdminCommunityResponse;
import com.jocoos.mybeautip.domain.community.dto.CommunityCategoryResponse;
import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCategory;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityCategoryDao;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityDao;
import com.jocoos.mybeautip.domain.community.vo.CommunitySearchCondition;
import com.jocoos.mybeautip.global.vo.SearchOption;
import com.jocoos.mybeautip.global.wrapper.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jocoos.mybeautip.domain.community.code.CommunityCategoryType.NOT_IN_ADMIN;
import static com.jocoos.mybeautip.domain.community.code.CommunityStatus.DELETE;
import static com.jocoos.mybeautip.domain.community.code.CommunityStatus.NORMAL;

@RequiredArgsConstructor
@Service
public class AdminCommunityService {

    private final CommunityCategoryDao categoryDao;
    private final CommunityDao communityDao;
    private final AdminCommunityConverter converter;
    private final CommunityConverter communityConverter;

    @Transactional(readOnly = true)
    public List<CommunityCategoryResponse> getCategories() {
        List<CommunityCategory> adminCategories = getAdminCategories();
        return converter.convert(adminCategories);
    }

    @Transactional(readOnly = true)
    public PageResponse<AdminCommunityResponse> getCommunities(Long categoryId, Pageable pageable, SearchOption searchOption) {
        CommunitySearchCondition condition = getSearchCondition(categoryId, pageable, searchOption);
        Page<AdminCommunityResponse> page = communityDao.getCommunitiesAllStatus(condition);
        return new PageResponse<>(page.getTotalElements(), page.getContent());
    }

    private CommunitySearchCondition getSearchCondition(Long categoryId, Pageable pageable, SearchOption searchOption) {
        return CommunitySearchCondition.builder()
                .pageable(pageable)
                .searchOption(searchOption)
                .categories(getCategories(categoryId))
                .build();
    }

    private List<CommunityCategory> getAdminCategories() {
        return categoryDao.getAllExcludeByTypes(NOT_IN_ADMIN);
    }

    private List<CommunityCategory> getCategories(Long categoryId) {
        if (categoryId == null) {
            return getAdminCategories();
        }
        return categoryDao.getCategoryForSearchCommunity(categoryId);
    }

    @Transactional(readOnly = true)
    public AdminCommunityResponse getCommunity(Long communityId) {
        Community community = communityDao.get(communityId);
        return converter.convert(community);
    }

    @Transactional
    public Long winCommunity(Long communityId, boolean isWin) {
        Community community = communityDao.get(communityId);
        community.win(isWin);
        return community.getId();
    }

    @Transactional
    public Long fixCommunity(Long communityId, boolean isFix) {
        if (isFix) {
            return communityDao.fix(communityId);
        }
        return communityDao.nonFix(communityId);
    }

    @Transactional
    public Long hideCommunity(Long communityId, boolean isHide) {
       if (isHide) {
           return communityDao.changeStatus(communityId, DELETE);
       }
       return communityDao.changeStatus(communityId, NORMAL);
    }
}
