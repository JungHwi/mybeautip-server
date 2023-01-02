package com.jocoos.mybeautip.domain.home.converter;

import com.jocoos.mybeautip.domain.community.converter.CommunityCategoryConverter;
import com.jocoos.mybeautip.domain.community.dto.CommunityResponse;
import com.jocoos.mybeautip.domain.home.vo.SummaryCommunityResult;
import com.jocoos.mybeautip.domain.member.converter.MemberConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CommunityCategoryConverter.class, MemberConverter.class})
public interface SummaryConverter {

    @Mappings({
            @Mapping(target = "id", source = "result.community.id"),
            @Mapping(target = "isWin", source = "result.community.isWin"),
            @Mapping(target = "status", source = "result.community.status"),
            @Mapping(target = "eventId", source = "result.community.eventId"),
            @Mapping(target = "eventTitle", source = "result.eventTitle"),
            @Mapping(target = "title", source = "result.community.title"),
            @Mapping(target = "contents", source = "result.community.contents"),
            @Mapping(target = "files", source = "result.thumbnailFile"),
            @Mapping(target = "votes", source = "result.voteResponses"),
            @Mapping(target = "viewCount", source = "result.community.viewCount"),
            @Mapping(target = "likeCount", source = "result.community.likeCount"),
            @Mapping(target = "reportCount", source = "result.community.reportCount"),
            @Mapping(target = "commentCount", source = "result.community.commentCount"),
            @Mapping(target = "createdAt", source = "result.community.createdAt"),
            @Mapping(target = "relationInfo", ignore = true),
            @Mapping(target = "member", source = "result.memberResponse"),
            @Mapping(target = "category", source = "result.community.category"),
            @Mapping(target = "sortedAt", ignore = true)
    })
    CommunityResponse convert(SummaryCommunityResult result);

    List<CommunityResponse> convert(List<SummaryCommunityResult> results);
}
