package com.jocoos.mybeautip.domain.home.converter;

import com.jocoos.mybeautip.domain.community.converter.CommunityCategoryConverter;
import com.jocoos.mybeautip.domain.community.dto.CommunityMemberResponse;
import com.jocoos.mybeautip.domain.community.dto.CommunityResponse;
import com.jocoos.mybeautip.domain.community.dto.VoteResponse;
import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.home.vo.SummaryCommunityResult;
import com.jocoos.mybeautip.domain.member.converter.MemberConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {CommunityCategoryConverter.class, MemberConverter.class})
public interface SummaryConverter {

    default List<CommunityResponse> convertBlindSummary(List<SummaryCommunityResult> results) {
        return results
                .stream()
                .map(result -> convertBlindSummary(result.getCommunity()))
                .collect(Collectors.toList());
    }

    default List<CommunityResponse> convertNormalSummary(List<SummaryCommunityResult> results) {
        return results
                .stream()
                .map(result -> convertNormalSummary(
                        result.getCommunity(),
                        result.getMemberResponse(),
                        result.getThumbnailUrl()))
                .collect(Collectors.toList());
    }

    default List<CommunityResponse> convertVoteSummary(List<SummaryCommunityResult> results) {
        return results
                .stream()
                .map(result -> convertVoteSummary(
                        result.getCommunity(),
                        result.getMemberResponse(),
                        result.getVoteResponses()))
                .collect(Collectors.toList());
    }

    default List<CommunityResponse> convertDripSummary(List<SummaryCommunityResult> results) {
        return results.stream()
                .map(result -> convertDripSummary(
                        result.getCommunity(),
                        result.getMemberResponse(),
                        result.getEventTitle(),
                        result.getThumbnailUrl()))
                .collect(Collectors.toList());
    }


    @Mapping(target = "contents", ignore = true)
    @Mapping(target = "member", ignore = true)
    @Mapping(target = "fileUrl", ignore = true)
    @Mapping(target = "votes", ignore = true)
    @Mapping(target = "relationInfo", ignore = true)
    CommunityResponse convertBlindSummary(Community community);

    @Mapping(target = "id", source = "community.id")
    @Mapping(target = "status", source = "community.status")
    @Mapping(target = "votes", source = "voteResponses")
    @Mapping(target = "member", source = "memberResponse")
    @Mapping(target = "fileUrl", ignore = true)
    @Mapping(target = "relationInfo", ignore = true)
    CommunityResponse convertVoteSummary(Community community, CommunityMemberResponse memberResponse, List<VoteResponse> voteResponses);

    @Mapping(target = "id", source = "community.id")
    @Mapping(target = "status", source = "community.status")
    @Mapping(target = "fileUrl", source = "thumbnailUrl")
    @Mapping(target = "member", source = "memberResponse")
    @Mapping(target = "votes", ignore = true)
    @Mapping(target = "relationInfo", ignore = true)
    CommunityResponse convertNormalSummary(Community community, CommunityMemberResponse memberResponse, List<String> thumbnailUrl);

    @Mapping(target = "id", source = "community.id")
    @Mapping(target = "status", source = "community.status")
    @Mapping(target = "title", source = "eventTitle")
    @Mapping(target = "fileUrl", source = "thumbnailUrl")
    @Mapping(target = "member", source = "memberResponse")
    @Mapping(target = "votes", ignore = true)
    @Mapping(target = "relationInfo", ignore = true)
    CommunityResponse convertDripSummary(Community community, CommunityMemberResponse memberResponse, String eventTitle, List<String> thumbnailUrl);
}
