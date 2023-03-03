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

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.jocoos.mybeautip.domain.community.code.CommunityCategoryType.BLIND;
import static com.jocoos.mybeautip.global.code.UrlDirectory.COMMUNITY;
import static com.jocoos.mybeautip.global.util.ImageUrlConvertUtil.toUrl;
import static java.time.ZonedDateTime.now;

@Mapper(componentModel = "spring", uses = {CommunityCategoryConverter.class, MemberConverter.class, CommunityFileConverter.class})
public interface CommunityConverter {

    @Mappings({
            @Mapping(target = "memberId", source = "member.id"),
            @Mapping(target = "communityFileList", source = "files"),
            @Mapping(target = "status", defaultValue = "NORMAL"),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "isWin", ignore = true),
            @Mapping(target = "viewCount", ignore = true),
            @Mapping(target = "likeCount", ignore = true),
            @Mapping(target = "commentCount", ignore = true),
            @Mapping(target = "reportCount", ignore = true),
            @Mapping(target = "sortedAt", ignore = true),
            @Mapping(target = "communityVoteList", ignore = true),
            @Mapping(target = "isTopFix", ignore = true)
    })
    Community convert(WriteCommunityRequest request);

    @AfterMapping
    default void afterMapping(@MappingTarget Community community) {
        if (!BLIND.equals(community.getCategory().getType())) {
            community.setTitle(null);
        }
        community.setSortedAt(now());
        community.setVote();
    }

    @Mappings({
            @Mapping(target = "fileUrl", source = "communityFile", qualifiedByName = "convertToUrl"),
            @Mapping(target = "count", source = "voteCount"),
            @Mapping(target = "isVoted", ignore = true),
    })
    VoteResponse convert(CommunityVote vote);

    @Mappings({
            @Mapping(target = "relationInfo", ignore = true),
            @Mapping(target = "files", ignore = true),
            @Mapping(target = "votes", ignore = true),
            @Mapping(target = "eventTitle", ignore = true)
    })
    CommunityResponse convert(Community community);

    @AfterMapping
    default void convert(@MappingTarget CommunityResponse.CommunityResponseBuilder response, Community community) {
        if (community.getCommunityVoteList().isEmpty()) {
            List<FileDto> fileDto = community.getCommunityFileList()
                    .stream()
                    .map(FileDto::from)
                    .toList();
            response.files(fileDto);
        } else {
            response.votes(voteResponsesFrom(community.getCommunityVoteList()));
            response.files(Collections.emptyList());
        }
    }

    List<CommunityResponse> convert(List<Community> community);

    @Mappings({
            @Mapping(target = "file", source = "communityFileList", qualifiedByName = "convert_community_main_file")
    })
    MyCommunityResponse convertToMyCommunity(Community community);

    List<MyCommunityResponse> convertToMyCommunity(List<Community> community);

    @Named("convert_community_main_file")
    default FileDto convertToUrl(List<CommunityFile> file) {
        if (CollectionUtils.isEmpty(file)) {
            return null;
        }
        return convertToFileDto(file.get(0));
    }

    default FileDto convertToFileDto(CommunityFile file) {
        return FileDto.from(file);
    }

    @Named(value = "convertToUrl")
    default String convertToUrl(CommunityFile file) {
        return toUrl(file.getFile(), COMMUNITY, file.getCommunity().getId());
    }

    default List<VoteResponse> voteResponsesFrom(List<CommunityVote> votes) {
        return votes.stream().map(this::convert).collect(Collectors.toList());
    }
}
