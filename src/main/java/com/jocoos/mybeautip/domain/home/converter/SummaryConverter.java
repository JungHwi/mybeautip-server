package com.jocoos.mybeautip.domain.home.converter;

import com.jocoos.mybeautip.domain.community.converter.CommunityCategoryConverter;
import com.jocoos.mybeautip.domain.community.dto.CommunityResponse;
import com.jocoos.mybeautip.domain.community.dto.VoteResponse;
import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityFile;
import com.jocoos.mybeautip.domain.community.persistence.domain.vote.CommunityVote;
import com.jocoos.mybeautip.domain.home.vo.SummaryResult;
import com.jocoos.mybeautip.domain.member.converter.MemberConverter;
import org.apache.commons.collections4.CollectionUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.jocoos.mybeautip.global.code.UrlDirectory.COMMUNITY;
import static com.jocoos.mybeautip.global.util.ImageUrlConvertUtil.toUrl;

@Mapper(componentModel = "spring", uses = {CommunityCategoryConverter.class, MemberConverter.class})
public abstract class SummaryConverter {

    public List<CommunityResponse> convertBlindSummary(List<SummaryResult> results) {
        return results
                .stream()
                .map(result -> convertBlindSummary(result.getCommunity()))
                .collect(Collectors.toList());
    }

    public List<CommunityResponse> convertNormalSummary(List<SummaryResult> results) {
        return results
                .stream()
                .map(result -> convertNormalSummary(result.getCommunity()))
                .collect(Collectors.toList());
    }

    public List<CommunityResponse> convertVoteSummary(List<SummaryResult> results) {
        return results
                .stream()
                .map(result -> convertVoteSummary(result.getCommunity()))
                .collect(Collectors.toList());
    }

    public List<CommunityResponse> convertDripSummary(List<SummaryResult> summaryResults) {
        return summaryResults.stream()
                .map(pickResult -> convertDripSummary(pickResult.getCommunity(), pickResult.getEventTitle()))
                .collect(Collectors.toList());
    }


    @Mapping(target = "contents", ignore = true)
    @Mapping(target = "member", ignore = true)
    abstract CommunityResponse convertBlindSummary(Community blind);

    @Mapping(target = "votes", source = "communityVoteList")
    abstract CommunityResponse convertVoteSummary(Community vote);

    @Mapping(target = "fileUrl", source = "communityFileList", qualifiedByName = "thumbnailToUrl")
    abstract CommunityResponse convertNormalSummary(Community community);

    @Mapping(target = "title", source = "eventTitle")
    @Mapping(target = "fileUrl", source = "community.communityFileList", qualifiedByName = "thumbnailToUrl")
    abstract CommunityResponse convertDripSummary(Community community, String eventTitle);


    @Mapping(target = "fileUrl", source = "communityFile", qualifiedByName = "toUrl")
    abstract VoteResponse convert(CommunityVote vote);


    @Named("toUrl")
    protected String convertToUrl(CommunityFile file) {
        return toUrl(file.getFile(), COMMUNITY, file.getCommunity().getId());
    }

    @Named("thumbnailToUrl")
    protected List<String> thumbnailToUrl(List<CommunityFile> files) {
        if (CollectionUtils.isEmpty(files)) {
            return Collections.emptyList();
        }
        String thumbnailUrl = toUrl(files.get(0).getFile(), COMMUNITY, files.get(0).getCommunity().getId());
        return Collections.singletonList(thumbnailUrl);
    }
}
