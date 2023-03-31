package com.jocoos.mybeautip.domain.community.converter;

import com.jocoos.mybeautip.domain.community.dto.CommunityResponse;
import com.jocoos.mybeautip.domain.member.dto.MyScrapResponse;
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
            @Mapping(target = "response.id", source = "community.id"),
            @Mapping(target = "response.isWin", source = "community.isWin"),
            @Mapping(target = "response.status", source = "community.status"),
            @Mapping(target = "response.title", source = "community.title"),
            @Mapping(target = "response.contents", source = "community.contents"),
            @Mapping(target = "response.files", source = "community.communityFileList"),
            @Mapping(target = "response.viewCount", source = "community.viewCount"),
            @Mapping(target = "response.likeCount", source = "community.likeCount"),
            @Mapping(target = "response.reportCount", source = "community.reportCount"),
            @Mapping(target = "response.commentCount", source = "community.commentCount"),
            @Mapping(target = "response.createdAt", source = "community.createdAt"),
            @Mapping(target = "response.member", source = "community.member"),
            @Mapping(target = "response.category", source = "community.category"),
            @Mapping(target = "response.relationInfo", ignore = true),
    })
    public abstract MyScrapResponse<CommunityResponse> convert(Scrap scrap, Community community);

    public List<MyScrapResponse<CommunityResponse>> convertVideoScrap(List<Scrap> scrapList, List<Video> videoList) {


        return null;
    }

    public List<MyScrapResponse<CommunityResponse>> convertCommunityScrap(List<Scrap> scrapList,
                                                                          List<Community> communityList) {
        Map<Long, Community> communityMap = communityList.stream()
                .collect(Collectors.toMap(Community::getId, Function.identity()));

        List<MyScrapResponse<CommunityResponse>> result = new ArrayList<>();
        for (Scrap scrap : scrapList) {
            MyScrapResponse<CommunityResponse> response = convert(scrap, communityMap.get(scrap.getRelationId()));
            result.add(response);
        }

        return result;
    }
}
