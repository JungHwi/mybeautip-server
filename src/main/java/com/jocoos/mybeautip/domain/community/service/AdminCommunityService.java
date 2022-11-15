package com.jocoos.mybeautip.domain.community.service;

import com.jocoos.mybeautip.domain.community.converter.AdminCommunityConverter;
import com.jocoos.mybeautip.domain.community.dto.AdminCommunityResponse;
import com.jocoos.mybeautip.domain.community.dto.CommunityCategoryResponse;
import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCategory;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityCategoryDao;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityDao;
import com.jocoos.mybeautip.domain.community.vo.CommunitySearchCondition;
import com.jocoos.mybeautip.domain.event.service.dao.EventDao;
import com.jocoos.mybeautip.global.vo.SearchOption;
import com.jocoos.mybeautip.global.wrapper.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jocoos.mybeautip.domain.community.code.CommunityCategoryType.NOT_IN_ADMIN;

@RequiredArgsConstructor
@Service
public class AdminCommunityService {

    private final CommunityCategoryDao categoryDao;
    private final CommunityDao communityDao;
    private final EventDao eventDao;
    private final AdminCommunityConverter converter;
    private final CommunityCommentDeleteService deleteService;


    @Transactional(readOnly = true)
    public List<CommunityCategoryResponse> getCategories() {
        List<CommunityCategory> adminCategories = getAdminCategories();
        return converter.convert(adminCategories);
    }

    @Transactional(readOnly = true)
    public PageResponse<AdminCommunityResponse> getCommunities(Long categoryId,
                                                               Long eventId,
                                                               Pageable pageable,
                                                               SearchOption searchOption) {
        CommunitySearchCondition condition = getSearchCondition(categoryId, eventId, pageable, searchOption);
        Page<AdminCommunityResponse> page = communityDao.getCommunitiesAllStatus(condition);
        return new PageResponse<>(page.getTotalElements(), page.getContent());
    }

    @Transactional(readOnly = true)
    public AdminCommunityResponse getCommunity(Long communityId) {
        Community community = communityDao.get(communityId);
        String eventTitle = getEventTitle(community.getEventId());
        return converter.convert(community, eventTitle);
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
        Community community = communityDao.get(communityId);
        community.hide(isHide);
        deleteService.hide(communityId, isHide);
        return community.getId();
    }

    private CommunitySearchCondition getSearchCondition(Long categoryId,
                                                        Long eventId,
                                                        Pageable pageable,
                                                        SearchOption searchOption) {
        return CommunitySearchCondition.builder()
                .pageable(pageable)
                .searchOption(searchOption)
                .eventId(eventId)
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

    private String getEventTitle(Long eventId) {
        if (eventId != null) {
            return eventDao.getEvent(eventId).getTitle();
        }
        return null;
    }
}
