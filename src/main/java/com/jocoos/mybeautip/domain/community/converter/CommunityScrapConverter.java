package com.jocoos.mybeautip.domain.community.converter;

import com.jocoos.mybeautip.domain.community.dto.CommunityScrapResponse;
import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.member.converter.MemberConverter;
import com.jocoos.mybeautip.domain.scrap.dto.ScrapResponse;
import com.jocoos.mybeautip.domain.scrap.persistence.domain.Scrap;
import com.jocoos.mybeautip.video.Video;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {CommunityCategoryConverter.class, MemberConverter.class, CommunityFileConverter.class})
public abstract class CommunityScrapConverter {

    @Mapping(target = "communityId", source = "scrap.relationId")
    public abstract ScrapResponse convert(Scrap scrap);

    @Mappings({
            @Mapping(target = "scrapId", source = "scrap.id"),
            @Mapping(target = "type", source = "scrap.type"),
            @Mapping(target = "communityResponse.id", source = "community.id"),
            @Mapping(target = "communityResponse.isWin", source = "community.isWin"),
            @Mapping(target = "communityResponse.status", source = "community.status"),
            @Mapping(target = "communityResponse.title", source = "community.title"),
            @Mapping(target = "communityResponse.contents", source = "community.contents"),
            @Mapping(target = "communityResponse.files", source = "community.communityFileList"),
            @Mapping(target = "communityResponse.viewCount", source = "community.viewCount"),
            @Mapping(target = "communityResponse.likeCount", source = "community.likeCount"),
            @Mapping(target = "communityResponse.reportCount", source = "community.reportCount"),
            @Mapping(target = "communityResponse.commentCount", source = "community.commentCount"),
            @Mapping(target = "communityResponse.createdAt", source = "community.createdAt"),
            @Mapping(target = "communityResponse.member", source = "community.member"),
            @Mapping(target = "communityResponse.category", source = "community.category"),
            @Mapping(target = "communityResponse.relationInfo", ignore = true),
    })
    public abstract CommunityScrapResponse convert(Scrap scrap, Community community);

    public List<CommunityScrapResponse> convertVideoScrap(List<Scrap> scrapList, List<Video> videoList) {


        return null;
    }

    public List<CommunityScrapResponse> convertCommunityScrap(List<Scrap> scrapList, List<Community> communityList) {
        Map<Long, Community> communityMap = communityList.stream()
                .collect(Collectors.toMap(Community::getId, Function.identity()));

        List<CommunityScrapResponse> result = new ArrayList<>();
        for (Scrap scrap : scrapList) {
            CommunityScrapResponse response = convert(scrap, communityMap.get(scrap.getRelationId()));
            result.add(response);
        }

        return result;
    }
}
