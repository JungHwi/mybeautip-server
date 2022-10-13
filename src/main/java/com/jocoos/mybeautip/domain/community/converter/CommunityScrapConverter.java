package com.jocoos.mybeautip.domain.community.converter;

import com.jocoos.mybeautip.domain.community.dto.CommunityScrapResponse;
import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityFile;
import com.jocoos.mybeautip.domain.member.converter.MemberConverter;
import com.jocoos.mybeautip.domain.scrap.dto.ScrapResponse;
import com.jocoos.mybeautip.domain.scrap.persistence.domain.Scrap;
import com.jocoos.mybeautip.video.Video;
import org.apache.commons.collections4.CollectionUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.jocoos.mybeautip.global.code.UrlDirectory.COMMUNITY;
import static com.jocoos.mybeautip.global.util.ImageUrlConvertUtil.toUrl;

@Mapper(componentModel = "spring", uses = {CommunityConverter.class, CommunityCategoryConverter.class, MemberConverter.class})
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
            @Mapping(target = "communityResponse.fileUrl", source = "community.communityFileList", qualifiedByName = "convert_community_main_file_wrap"),
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

    @Named("convert_community_main_file_wrap")
    protected List<String> convertToUrlWrap(List<CommunityFile> file) {
        if (CollectionUtils.isEmpty(file)) {
            return Collections.emptyList();
        }
        return Collections.singletonList(convertToUrl(file.get(0)));
    }

    private String convertToUrl(CommunityFile file) {
        return toUrl(file.getFile(), COMMUNITY, file.getCommunity().getId());
    }
}
