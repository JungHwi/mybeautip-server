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
import com.jocoos.mybeautip.global.wrapper.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jocoos.mybeautip.domain.community.code.CommunityCategoryType.NOT_IN_ADMIN;

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
    public PageResponse<AdminCommunityResponse> getCommunities(Long categoryId, CommunitySearchCondition condition) {
        setCategories(categoryId, condition);
        Page<AdminCommunityResponse> page = communityDao.getCommunitiesInAllStatus(condition);
        return new PageResponse<>(page.getTotalElements(), page.getContent());
    }

    private List<CommunityCategory> getAdminCategories() {
        return categoryDao.getAllExcludeByTypes(NOT_IN_ADMIN);
    }

    private void setCategories(Long categoryId, CommunitySearchCondition condition) {
        condition.setCategories(getCategories(categoryId));
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
}
