package com.jocoos.mybeautip.domain.community.converter;

import com.jocoos.mybeautip.domain.community.dto.CommunityResponse;
import com.jocoos.mybeautip.domain.community.dto.WriteCommunityRequest;
import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityFile;
import com.jocoos.mybeautip.domain.member.converter.MemberConverter;
import com.jocoos.mybeautip.global.dto.FileDto;
import org.apache.commons.collections4.CollectionUtils;
import org.mapstruct.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

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
            @Mapping(target = "category", ignore = true)
    })
    Community convert(WriteCommunityRequest request);

    @AfterMapping
    default void convert(@MappingTarget Community community, WriteCommunityRequest request) {
        community.setSortedAt(ZonedDateTime.now());

        if (CollectionUtils.isNotEmpty(request.getFiles())) {
            List<CommunityFile> communityFileList = new ArrayList<>();
            for (FileDto requestFile : request.getFiles()) {
                CommunityFile communityFile = new CommunityFile(getFilename(requestFile.getUrl()));
                communityFileList.add(communityFile);
            }
            community.setCommunityFileList(communityFileList);
        }
    }

    @Mappings({
            @Mapping(target = "fileUrl", source = "communityFileList", qualifiedByName = "convert_community_file"),
            @Mapping(target = "relationInfo", ignore = true),
    })
    CommunityResponse convert(Community community);

    List<CommunityResponse> convert(List<Community> community);

    @Named("convert_community_file")
    default String convertToUrl(CommunityFile file) {
        return toUrl(file.getFile(), COMMUNITY, file.getCommunity().getId());
    }
}
