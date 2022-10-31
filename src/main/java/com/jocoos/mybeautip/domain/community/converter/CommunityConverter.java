package com.jocoos.mybeautip.domain.community.converter;

import com.jocoos.mybeautip.domain.community.dto.CommunityResponse;
import com.jocoos.mybeautip.domain.community.dto.VoteResponse;
import com.jocoos.mybeautip.domain.community.dto.WriteCommunityRequest;
import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityFile;
import com.jocoos.mybeautip.domain.community.persistence.domain.vote.CommunityVote;
import com.jocoos.mybeautip.domain.member.converter.MemberConverter;
import com.jocoos.mybeautip.domain.member.dto.MyCommunityResponse;
import com.jocoos.mybeautip.global.dto.FileDto;
import org.apache.commons.collections4.CollectionUtils;
import org.mapstruct.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.jocoos.mybeautip.domain.community.code.CommunityCategoryType.VOTE;
import static com.jocoos.mybeautip.global.code.UrlDirectory.COMMUNITY;
import static com.jocoos.mybeautip.global.util.FileUtil.getFilename;
import static com.jocoos.mybeautip.global.util.ImageUrlConvertUtil.toUrl;

@Mapper(componentModel = "spring", uses = {CommunityCategoryConverter.class, MemberConverter.class})
public interface CommunityConverter {

    @Mappings({
            @Mapping(target = "memberId", source = "member.id"),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "isWin", ignore = true),
            @Mapping(target = "status", constant = "NORMAL"),
            @Mapping(target = "viewCount", ignore = true),
            @Mapping(target = "likeCount", ignore = true),
            @Mapping(target = "commentCount", ignore = true),
            @Mapping(target = "reportCount", ignore = true),
            @Mapping(target = "sortedAt", ignore = true),
            @Mapping(target = "communityFileList", ignore = true),
            @Mapping(target = "communityVoteList", ignore = true),
            @Mapping(target = "isTopFix", ignore = true)
    })
    Community convert(WriteCommunityRequest request);

    @AfterMapping
    default void convert(@MappingTarget Community community, WriteCommunityRequest request) {
        community.setSortedAt(ZonedDateTime.now());

        if (CollectionUtils.isNotEmpty(request.getFiles())) {
            setFileAndVote(community, request);
        }
    }

    @Mappings({
            @Mapping(target = "fileUrl", source = "communityFile", qualifiedByName = "convertToUrl"),
            @Mapping(target = "count", source = "voteCount"),
            @Mapping(target = "isVoted", ignore = true),
    })
    VoteResponse convert(CommunityVote vote);

    @Mappings({
            @Mapping(target = "relationInfo", ignore = true),
            @Mapping(target = "fileUrl", ignore = true),
            @Mapping(target = "votes", ignore = true),
            @Mapping(target = "eventTitle", ignore = true)
    })
    CommunityResponse convert(Community community);

    @AfterMapping
    default void convert(@MappingTarget CommunityResponse.CommunityResponseBuilder response, Community community) {
        if (community.getCommunityVoteList().isEmpty()) {
            response.fileUrl(fileUrlsFrom(community.getCommunityFileList()));
        } else {
            response.votes(voteResponsesFrom(community.getCommunityVoteList()));
            response.fileUrl(Collections.emptyList());
        }
    }

    List<CommunityResponse> convert(List<Community> community);

    @Mappings({
            @Mapping(target = "fileUrl", source = "communityFileList", qualifiedByName = "convert_community_main_file")
    })
    MyCommunityResponse convertToMyCommunity(Community community);

    List<MyCommunityResponse> convertToMyCommunity(List<Community> community);

    @Named("convert_community_main_file")
    default String convertToUrl(List<CommunityFile> file) {
        if (CollectionUtils.isEmpty(file)) {
            return null;
        }
        return convertToUrl(file.get(0));
    }

    @Named(value = "convertToUrl")
    default String convertToUrl(CommunityFile file) {
        return toUrl(file.getFile(), COMMUNITY, file.getCommunity().getId());
    }

    default void setFileAndVote(Community community, WriteCommunityRequest request) {
        if (VOTE.equals(request.getCategory().getType())) {
            setCommunityFileAndVote(community, request);
        } else {
            setCommunityFile(community, request);
        }
    }

    default List<CommunityFile> setCommunityFile(Community community, WriteCommunityRequest request) {
        List<CommunityFile> communityFileList = new ArrayList<>();
        for (FileDto requestFile : request.getFiles()) {
            CommunityFile communityFile = new CommunityFile(getFilename(requestFile.getUrl()));
            communityFileList.add(communityFile);
        }
        community.setCommunityFileList(communityFileList);
        return communityFileList;
    }

    default void setCommunityFileAndVote(Community community, WriteCommunityRequest request) {
        List<CommunityFile> communityFiles = setCommunityFile(community, request);
        List<CommunityVote> communityVotes = communityFiles.stream()
                .map(file -> new CommunityVote(community, file))
                .collect(Collectors.toList());
        community.setCommunityVoteList(communityVotes);
    }

    default List<String> fileUrlsFrom(List<CommunityFile> files) {
        return files.stream().map(this::convertToUrl).collect(Collectors.toList());
    }

    default List<VoteResponse> voteResponsesFrom(List<CommunityVote> votes) {
        return votes.stream().map(this::convert).collect(Collectors.toList());
    }
}
