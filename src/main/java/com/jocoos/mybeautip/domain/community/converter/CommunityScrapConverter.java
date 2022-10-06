package com.jocoos.mybeautip.domain.community.converter;

import com.jocoos.mybeautip.domain.community.dto.CommunityScrapResponse;
import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.member.converter.MemberConverter;
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

@Mapper(componentModel = "spring", uses = {CommunityConverter.class, CommunityCategoryConverter.class, MemberConverter.class})
public abstract class CommunityScrapConverter {

    @Mappings({
            @Mapping(target = "communityId", source = "relationId"),
    })
    public abstract CommunityScrapResponse convert(Scrap scrap);

    @Mappings({
            @Mapping(target = "id", source = "scrap.id"),
            @Mapping(target = "type", source = "scrap.type"),
            @Mapping(target = "communityId", source = "community.id"),
            @Mapping(target = "isScrap", source = "scrap.isScrap"),
            @Mapping(target = "isWin", source = "community.isWin"),
            @Mapping(target = "communityStatus", source = "community.status"),
            @Mapping(target = "title", source = "community.title"),
            @Mapping(target = "contents", source = "community.contents"),
            @Mapping(target = "fileUrl", source = "community.communityFileList", qualifiedByName = "convert_community_main_file"),
            @Mapping(target = "viewCount", source = "community.viewCount"),
            @Mapping(target = "likeCount", source = "community.likeCount"),
            @Mapping(target = "reportCount", source = "community.reportCount"),
            @Mapping(target = "commentCount", source = "community.commentCount"),
            @Mapping(target = "createdAt", source = "community.createdAt"),
            @Mapping(target = "member", source = "community.member"),
            @Mapping(target = "category", source = "community.category"),
            @Mapping(target = "relationInfo", ignore = true),
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
