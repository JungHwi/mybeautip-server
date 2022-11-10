package com.jocoos.mybeautip.domain.community.converter;

import com.jocoos.mybeautip.domain.community.dto.AdminCommunityResponse;
import com.jocoos.mybeautip.domain.community.dto.CommunityCategoryResponse;
import com.jocoos.mybeautip.domain.community.dto.CommunityMemberResponse;
import com.jocoos.mybeautip.domain.community.dto.VoteResponse;
import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class AdminCommunityConverter {

    public abstract List<CommunityCategoryResponse> convert(List<CommunityCategory> adminCategories);

    @Mapping(target = "type", ignore = true)
    @Mapping(target = "hint", ignore = true)
    protected abstract CommunityCategoryResponse convert(CommunityCategory adminCategory);


    public AdminCommunityResponse convert(Community community) {
        AdminCommunityResponse response = getResponse(community);

        if (!community.isVotesEmpty()) {
            response.setVotesAndClearFileUrls(getVoteResponses(community));
        }

        return response;
    }

    private List<VoteResponse> getVoteResponses(Community community) {
        return community.getCommunityVoteList()
                .stream()
                .map(VoteResponse::from)
                .toList();
    }

    private AdminCommunityResponse getResponse(Community community) {
        CommunityCategoryResponse categoryResponse = CommunityCategoryResponse.from(community.getCategory());
        CommunityMemberResponse memberResponse = CommunityMemberResponse.from(community.getMember());
        return toResponse(community, categoryResponse, memberResponse);
    }

    private AdminCommunityResponse toResponse(Community community,
                                                     CommunityCategoryResponse categoryResponse,
                                                     CommunityMemberResponse memberResponse) {
        return new AdminCommunityResponse(
                community,
                community.getCommunityFileList(),
                categoryResponse,
                memberResponse);
    }
}