package com.jocoos.mybeautip.domain.community.service;

import com.jocoos.mybeautip.domain.community.code.CommunityStatus;
import com.jocoos.mybeautip.domain.community.converter.AdminCommunityConverter;
import com.jocoos.mybeautip.domain.community.dto.AdminCommunityResponse;
import com.jocoos.mybeautip.domain.community.dto.CommunityCategoryResponse;
import com.jocoos.mybeautip.domain.community.dto.PatchCommunityRequest;
import com.jocoos.mybeautip.domain.community.dto.WriteCommunityRequest;
import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCategory;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityCategoryDao;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityDao;
import com.jocoos.mybeautip.domain.community.vo.CommunitySearchCondition;
import com.jocoos.mybeautip.domain.event.service.dao.EventDao;
import com.jocoos.mybeautip.global.dto.FileDto;
import com.jocoos.mybeautip.global.vo.SearchOption;
import com.jocoos.mybeautip.global.wrapper.PageResponse;
import com.jocoos.mybeautip.member.Member;
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
    private final CommunityCommentDeleteService commentDeleteService;
    private final CommunityFileService fileService;

    @Transactional
    public AdminCommunityResponse write(WriteCommunityRequest request, Member member) {
        request.setMember(member);
        request.setFileOperationToUpload();
        Community community = communityDao.write(request);
        fileService.writeWithTranscode(request.getFiles(), community.getId());
        return converter.convert(community);
    }


    @Transactional(readOnly = true)
    public List<CommunityCategoryResponse> getCategories() {
        List<CommunityCategory> adminCategories = getAdminCategories();
        return converter.convert(adminCategories);
    }

    @Transactional(readOnly = true)
    public PageResponse<AdminCommunityResponse> getCommunities(CommunityStatus status,
                                                               Long categoryId,
                                                               Long eventId,
                                                               Pageable pageable,
                                                               SearchOption searchOption) {
        CommunitySearchCondition condition = getSearchCondition(status, categoryId, eventId, pageable, searchOption);
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
    public Long edit(Long communityId, PatchCommunityRequest request) {
        Community community = communityDao.get(communityId);
        community.edit(request.getTitle(), request.getContents());
        editFiles(request, community);
        communityDao.save(community);
        return community.getId();
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
        commentDeleteService.hide(communityId, isHide);
        return community.getId();
    }

    @Transactional
    public Long delete(Long communityId) {
        Community community = communityDao.get(communityId);
        community.deleteAdminWrite();
        commentDeleteService.delete(community.getId());
        return community.getId();
    }

    private void editFiles(PatchCommunityRequest request, Community community) {
        List<FileDto> editFiles = request.getFileDto(community.getCommunityFileList());
        fileService.editFilesWithTranscode(community, editFiles);
        community.sortFilesByRequestIndex(request.getImageUrls());
    }

    private CommunitySearchCondition getSearchCondition(CommunityStatus status,
                                                        Long categoryId,
                                                        Long eventId,
                                                        Pageable pageable,
                                                        SearchOption searchOption) {
        return CommunitySearchCondition.builder()
                .status(status)
                .eventId(eventId)
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
